package com.dev.minn.identity.service;

import com.dev.minn.common.exception.AppException;
import com.dev.minn.common.exception.CodeException;
import com.dev.minn.grpc.profile.CreateProfileRequest;
import com.dev.minn.identity.client.ProfileClient;
import com.dev.minn.identity.constant.AccountStatus;
import com.dev.minn.identity.constant.NotificationEventType;
import com.dev.minn.identity.dto.PendingAccountInfo;
import com.dev.minn.identity.dto.request.AuthenticationRequest;
import com.dev.minn.identity.dto.request.LogoutRequest;
import com.dev.minn.identity.dto.request.RegistrationInitRequest;
import com.dev.minn.identity.dto.request.RegistrationVerifyRequest;
import com.dev.minn.identity.dto.response.AccountResponse;
import com.dev.minn.identity.dto.response.AuthenticationResponse;
import com.dev.minn.identity.entity.Account;
import com.dev.minn.identity.event.NotificationDispatchEvent;
import com.dev.minn.identity.event.UserCreatedEvent;
import com.dev.minn.identity.mapper.AccountMapper;
import com.dev.minn.identity.repository.AccountRepository;
import com.dev.minn.identity.repository.RoleRepository;
import com.dev.minn.identity.utils.SecurityUtils;
import io.grpc.StatusRuntimeException;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
@Transactional(readOnly = true)
public class AuthenticationService {

    ApplicationEventPublisher eventPublisher;

    PasswordEncoder passwordEncoder;

    AccountRepository accountRepository;

    JwtService jwtService;
    OtpService otpService;
    RoleRepository roleRepository;
    RedisService redisService;

    ProfileClient profileClient;

    AccountMapper accountMapper;

    private static final String PENDING_ACCOUNT_PREFIX = "registration:pending:";
    private static final long OTP_INFO_TTL = 5 * 60;
    private static final long PENDING_INFO_TTL = 7 * 60;

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        Account account = accountRepository.findByEmail(request.getEmail())
                .orElseThrow(CodeException.USER_NOT_FOUND::throwException);

        boolean isMatchingPassword = passwordEncoder.matches(request.getPassword(), account.getPassword());

        if (!isMatchingPassword)
            throw CodeException.INVALID_CREDENTIALS.throwException();

        String accessToken = jwtService.generateToken(account, JwtService.TokenType.ACCESS);
        String refreshToken = jwtService.generateToken(account, JwtService.TokenType.REFRESH);

        return AuthenticationResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .authenticated(true)
                .build();
    }

    public void logout(LogoutRequest request) {
        jwtService.revokeToken(jwtService.verifyToken(request.getAccessToken(), JwtService.TokenType.ACCESS));
        jwtService.revokeToken(jwtService.verifyToken(request.getRefreshToken(), JwtService.TokenType.REFRESH));
    }

    public void initiateRegistration(RegistrationInitRequest request) {
        String email = request.getEmail();
        String password = request.getPassword();

        if (accountRepository.existsByEmail(email))
            throw CodeException.EMAIL_ALREADY_EXISTS.throwException();

        if (otpService.isOtpCached(email, OtpService.Action.REGISTER))
            throw CodeException.REGISTRATION_PENDING.throwException();

        PendingAccountInfo pendingInfo = PendingAccountInfo.builder()
                .email(email)
                .hashedPassword(passwordEncoder.encode(password))
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .build();

        String otp = OtpService.generateSecureOtp();

        otpService.cacheOtp(OtpService.Action.REGISTER, email, otp, OTP_INFO_TTL, TimeUnit.SECONDS);

        redisService.set(
                PENDING_ACCOUNT_PREFIX + email,
                pendingInfo,
                PENDING_INFO_TTL,
                TimeUnit.SECONDS
        );

        Map<String, Object> payload = Map.of(
                "email", email,
                "name", request.getFirstName() + " " + request.getLastName(),
                "otp", otp
        );

        eventPublisher.publishEvent(
                new NotificationDispatchEvent(this, NotificationEventType.SEND_OTP_VERIFY, payload)
        );
    }

    @Transactional
    public AccountResponse confirmRegistration(RegistrationVerifyRequest request) {
        boolean isOtpValid = otpService.verifyAndClearOtp(OtpService.Action.REGISTER, request.getEmail(), request.getOtp());

        if (!isOtpValid)
            throw new AppException(CodeException.OTP_INVALID);

        PendingAccountInfo accountInfo = redisService.getAndClear(PENDING_ACCOUNT_PREFIX + request.getEmail(), PendingAccountInfo.class);

        if (accountInfo == null)
            throw new RuntimeException("Account info not found");

        if (accountRepository.existsByEmail(accountInfo.getEmail()))
            throw CodeException.EMAIL_ALREADY_EXISTS.throwException();

        Account account = accountMapper.toEntity(accountInfo);
        account.addRole(roleRepository.findByName("USER").orElseThrow(CodeException.ROLE_NOT_FOUND::throwException));
        account.setStatus(AccountStatus.ACTIVE);

        accountRepository.save(account);

        try {
            var response = profileClient.createProfile(CreateProfileRequest.newBuilder()
                    .setAccountId(account.getId().toString())
                    .setEmail(account.getEmail())
                    .setFirstName(accountInfo.getFirstName())
                    .setLastName(accountInfo.getLastName())
                    .build());

            System.out.println("Profile ID: " + response.getProfileId() + "status: " + response.getStatus() + "");

            Map<String, Object> payload = Map.of(
                    "email", account.getEmail(),
                    "name", accountInfo.getFirstName() + " " + accountInfo.getLastName()
            );

            eventPublisher.publishEvent(
                    new NotificationDispatchEvent(this, NotificationEventType.USER_WELCOME, payload)
            );

            eventPublisher.publishEvent(new UserCreatedEvent(
                    account.getId().toString(),
                    response.getProfileId(),
                    account.getEmail(),
                    accountInfo.getFirstName() + " " + accountInfo.getLastName()
            ));

        } catch (StatusRuntimeException e) {
            throw new AppException(CodeException.PROFILE_CREATION_FAILED);
        }

        AccountResponse accountResponse = accountMapper.toResponse(account);
        accountResponse.setFirstName(accountInfo.getFirstName());
        accountResponse.setLastName(accountInfo.getLastName());

        return accountResponse;
    }

    @Transactional
    public void softDelete(UUID accountId) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(CodeException.USER_NOT_FOUND::throwException);

        if (account.getStatus() == AccountStatus.DELETED)
            throw new AppException(CodeException.ACCOUNT_DELETED);

        account.setStatus(AccountStatus.DELETED);
        account.setDeletedAt(Instant.now());
        account.setDeletedBy(SecurityUtils.getCurrentAccountId());
        account.setEmail(account.getEmail() + ".deleted." + UUID.randomUUID().toString());

        jwtService.lockSession(accountId.toString());

        accountRepository.save(account);
    }

    @Transactional
    public void hardDelete(UUID accountId) {
        Account account = accountRepository.getReferenceById(accountId);
        accountRepository.deleteById(accountId);
    }

    @Transactional
    public void changeStatus(UUID accountId, AccountStatus status) {
        Account account = accountRepository.findById(accountId)
                .orElseThrow(CodeException.USER_NOT_FOUND::throwException);
        account.setStatus(status);
        accountRepository.save(account);
    }
}

