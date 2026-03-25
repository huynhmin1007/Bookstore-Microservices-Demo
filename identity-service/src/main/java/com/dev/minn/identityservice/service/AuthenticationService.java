package com.dev.minn.identityservice.service;

import com.dev.minn.identityservice.constant.AccountStatus;
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
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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

    public AuthenticationResponse authenticate(AuthenticationRequest request) {
        Account account = accountRepository.findByEmail(request.getEmail())
                .orElseThrow(CodeException.USER_NOT_FOUND::throwException);

        boolean isMatchingPassword = passwordEncoder.matches(request.getPassword(), account.getPassword());

        if(!isMatchingPassword)
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

    private static final String PENDING_ACCOUNT_PREFIX = "registration:pending:";
    private static final long OTP_INFO_TTL = 5 * 60;
    private static final long PENDING_INFO_TTL = 7 * 60;

    public void initiateRegistration(RegistrationInitRequest request) {
        String email = request.getEmail();
        String password = request.getPassword();

        if (accountRepository.existsByEmail(email))
            throw CodeException.EMAIL_ALREADY_EXISTS.throwException();

        if (otpService.isOtpCached(email))
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
        account.setStatus(AccountStatus.ACTIVE);

        accountRepository.save(account);

        eventPublisher.publishEvent(
                new SendWelcomeEmailEvent(account.getEmail())
        );

        return accountMapper.toSummary(account);
    }
}
