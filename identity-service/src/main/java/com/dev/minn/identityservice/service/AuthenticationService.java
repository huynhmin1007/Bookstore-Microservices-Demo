package com.dev.minn.identityservice.service;

import com.dev.minn.grpc.profile.CreateProfileRequest;
import com.dev.minn.grpc.profile.CreateProfileResponse;
import com.dev.minn.grpc.profile.ProfileServiceGrpcGrpc;
import com.dev.minn.identityservice.client.ProfileClient;
import com.dev.minn.identityservice.client.dto.UserProfileCreateRequest;
import com.dev.minn.identityservice.client.dto.UserProfileSummary;
import com.dev.minn.identityservice.constant.AccountStatus;
import com.dev.minn.identityservice.dto.ApiResponse;
import com.dev.minn.identityservice.dto.PendingAccountInfo;
import com.dev.minn.identityservice.dto.request.AuthenticationRequest;
import com.dev.minn.identityservice.dto.request.LogoutRequest;
import com.dev.minn.identityservice.dto.request.RegistrationInitRequest;
import com.dev.minn.identityservice.dto.request.RegistrationVerifyRequest;
import com.dev.minn.identityservice.dto.response.AccountSummary;
import com.dev.minn.identityservice.dto.response.AuthenticationResponse;
import com.dev.minn.identityservice.entity.Account;
import com.dev.minn.identityservice.event.SendRegistrationOtpEmailEvent;
import com.dev.minn.identityservice.event.SendWelcomeEmailEvent;
import com.dev.minn.identityservice.exception.AppException;
import com.dev.minn.identityservice.exception.CodeException;
import com.dev.minn.identityservice.mapper.AccountMapper;
import com.dev.minn.identityservice.repository.AccountRepository;
import com.dev.minn.identityservice.utils.SecurityUtils;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import net.devh.boot.grpc.client.inject.GrpcClient;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Instant;
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
    RoleService roleService;
    RedisService redisService;

    AccountMapper accountMapper;

    private static final String PENDING_ACCOUNT_PREFIX = "registration:pending:";
    private static final long OTP_INFO_TTL = 5 * 60;
    private static final long PENDING_INFO_TTL = 7 * 60;

    ProfileClient profileClient;

    @NonFinal
    @GrpcClient("profile-service")
    ProfileServiceGrpcGrpc.ProfileServiceGrpcBlockingStub profileGrpcStub;

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
                .build();

        String otp = OtpService.generateSecureOtp();

        otpService.cacheOtp(OtpService.Action.REGISTER, email, otp, OTP_INFO_TTL, TimeUnit.SECONDS);

        redisService.set(
                PENDING_ACCOUNT_PREFIX + email,
                pendingInfo,
                PENDING_INFO_TTL,
                TimeUnit.SECONDS
        );

        eventPublisher.publishEvent(new SendRegistrationOtpEmailEvent(email, otp, OTP_INFO_TTL));
    }

    @Transactional
    public AccountSummary confirmRegistration(RegistrationVerifyRequest request) {
        boolean isOtpValid = otpService.verifyAndClearOtp(OtpService.Action.REGISTER, request.getEmail(), request.getOtp());

        if (!isOtpValid)
            throw new AppException(CodeException.OTP_INVALID);

        PendingAccountInfo accountInfo = redisService.getAndClear(PENDING_ACCOUNT_PREFIX + request.getEmail(), PendingAccountInfo.class);

        if (accountInfo == null)
            throw new RuntimeException("Account info not found");

        if (accountRepository.existsByEmail(accountInfo.getEmail()))
            throw CodeException.EMAIL_ALREADY_EXISTS.throwException();

        Account account = accountMapper.toEntity(accountInfo);
        account.addRole(roleService.findRoleByName("USER"));
        account.setStatus(AccountStatus.PENDING);

        accountRepository.save(account);

//        createProfile_OpenFeign(
//                account.getId(),
//                UserProfileCreateRequest.builder()
//                        .userId(account.getId().toString())
//                        .firstName("Huỳnh")
//                        .lastName("Minh")
//                        .build()
//        );

        createProfile_Grpc(
                account.getId(),
                UserProfileCreateRequest.builder()
                        .userId(account.getId().toString())
                        .firstName("Huỳnh")
                        .lastName("Minh")
                        .build()
        );

        eventPublisher.publishEvent(
                new SendWelcomeEmailEvent(account.getEmail())
        );

        return accountMapper.toSummary(account);
    }

    @Transactional
    public void createProfile_OpenFeign(UUID accountId, UserProfileCreateRequest request) {
        try {
            ApiResponse<UserProfileSummary> response = profileClient.createProfile(
                    accountId.toString(),
                    "ROLE_SYSTEM",
                    "*:*:*",
                    request
            );
            accountRepository.getReferenceById(accountId).setStatus(AccountStatus.ACTIVE);
            log.info("Create profile success: " + response.getData());
        } catch (Exception e) {
            accountRepository.deleteById(accountId);
            log.error("Failure when to call Profile Service: {}", e.getMessage());
            throw new RuntimeException("Create profile failed, delete account: " + accountId.toString());
        }
    }

    @Transactional
    public void createProfile_Grpc(UUID accountId, UserProfileCreateRequest request) {
        try {
            CreateProfileRequest grpcRequest = CreateProfileRequest.newBuilder()
                    .setAccountId(accountId.toString())
                    .setFirstName(request.getFirstName())
                    .setLastName(request.getLastName())
                    .build();

            CreateProfileResponse response = profileGrpcStub.createProfile(grpcRequest);
            log.info("Create profile success: " + response.getStatus());
            accountRepository.getReferenceById(accountId).setStatus(AccountStatus.ACTIVE);
        } catch (Exception e) {
            accountRepository.deleteById(accountId);
            log.error("Failure when to call Profile Service: {}", e.getMessage());
            throw new RuntimeException("Create profile failed, delete account: " + accountId.toString());
        }
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
}

