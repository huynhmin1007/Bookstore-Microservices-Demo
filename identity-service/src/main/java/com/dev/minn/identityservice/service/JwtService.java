package com.dev.minn.identityservice.service;

import com.dev.minn.identityservice.config.RsaKeyConfig;
import com.dev.minn.identityservice.entity.Account;
import com.dev.minn.identityservice.exception.AppException;
import com.dev.minn.identityservice.exception.CodeException;
import com.dev.minn.identityservice.repository.PermissionRepository;
import com.nimbusds.jose.JOSEException;
import com.nimbusds.jose.JOSEObjectType;
import com.nimbusds.jose.JWSAlgorithm;
import com.nimbusds.jose.JWSHeader;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.text.ParseException;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class JwtService {

    final static String BLACKLIST_PREFIX = "blacklist:jti:";
    final static String SESSION_PREFIX = "session:";
    final static String BANNED_SESSION_PREFIX = "banned_session:";

    @NonFinal
    @Value("${jwt.access-token-validity}")
    Long ACCESS_TOKEN_VALIDITY;

    @NonFinal
    @Value("${jwt.refresh-token-validity}")
    Long REFRESH_TOKEN_VALIDITY;

    @NonFinal
    @Value("${jwt.issuer}")
    String ISSUER;

    RsaKeyConfig rsaKeys;
    RedisService redisService;

    PermissionRepository permissionRepository;

    public String generateToken(Account account, TokenType type) {
        try {
            JWSHeader header = new JWSHeader.Builder(JWSAlgorithm.RS256)
                    .type(JOSEObjectType.JWT)
                    .build();

            Instant now = Instant.now();
            boolean isAccessToken = type.equals(TokenType.ACCESS);
            long validity = isAccessToken ? ACCESS_TOKEN_VALIDITY : REFRESH_TOKEN_VALIDITY;

            JWTClaimsSet claimsSet = new JWTClaimsSet.Builder()
                    .issuer(ISSUER)
                    .subject(account.getId().toString())
                    .issueTime(Date.from(now))
                    .expirationTime(Date.from(now.plusSeconds(validity)))
                    .jwtID(UUID.randomUUID().toString())
                    .claim("scope", buildScope(account))
                    .claim("email", account.getEmail())
                    .claim("tokenType", isAccessToken ? "access" : "refresh")
                    .build();

            SignedJWT signedJWT = new SignedJWT(header, claimsSet);
            signedJWT.sign(new RSASSASigner(rsaKeys.privateKey()));

            return signedJWT.serialize();
        } catch (JOSEException e) {
            log.error("Failed to sign JWT for account: {}", account.getEmail(), e);
            throw new RuntimeException("Internal error while generating token");
        }
    }

    public SignedJWT verifyToken(String token, TokenType expectedTokenType) {
        try {
            SignedJWT signedJWT = SignedJWT.parse(token);
            JWTClaimsSet claimsSet = signedJWT.getJWTClaimsSet();

            RSASSAVerifier verifier = new RSASSAVerifier(rsaKeys.publicKey());
            boolean isSignatureValid = signedJWT.verify(verifier);
            boolean isNotExpired = new Date().before(claimsSet.getExpirationTime());

            if (!isSignatureValid || !isNotExpired) {
                throw new AppException(CodeException.TOKEN_INVALID);
            }

            String jti = claimsSet.getJWTID();
            String tokenTypeStr = claimsSet.getStringClaim("tokenType");

            boolean isBusinessValid = validateBusinessRules(signedJWT, expectedTokenType);

            if (!isBusinessValid) {
                throw new AppException(CodeException.TOKEN_INVALID);
            }

            return signedJWT;
        } catch (ParseException | JOSEException e) {
            log.warn("Lỗi parse hoặc verify token: {}", e.getMessage());
            throw new AppException(CodeException.TOKEN_INVALID);
        }
    }

    private boolean validateCoreRules(String accountId, String jti, String tokenType, Instant issueTime, TokenType expectedTokenType) {
        boolean isMatchType = expectedTokenType.match(tokenType);

        boolean isBlacklisted = redisService.exists(BLACKLIST_PREFIX + jti);

        String bannedEpochStr = redisService.get(BANNED_SESSION_PREFIX + accountId, String.class);

        if (bannedEpochStr != null && issueTime != null) {
            long bannedEpoch = Long.parseLong(bannedEpochStr);
            long tokenEpoch = issueTime.toEpochMilli();

            if (tokenEpoch <= bannedEpoch) {
                log.warn("Blocked revoked token for account: {}", accountId);
                return false;
            }
        }

        return isMatchType && !isBlacklisted;
    }

    public boolean validateBusinessRules(Jwt jwt, TokenType expectedTokenType) {
        String accountId = jwt.getSubject();
        String jti = jwt.getId();
        String tokenType = jwt.getClaimAsString("tokenType");
        Instant issueTime = jwt.getIssuedAt();

        return validateCoreRules(accountId, jti, tokenType, issueTime, expectedTokenType);
    }

    public boolean validateBusinessRules(SignedJWT signedJWT, TokenType expectedTokenType) {
        try {
            JWTClaimsSet claims = signedJWT.getJWTClaimsSet();

            String accountId = claims.getSubject();
            String jti = claims.getJWTID();
            String tokenType = claims.getStringClaim("tokenType");
            Instant issueTime = claims.getIssueTime() != null ? claims.getIssueTime().toInstant() : null;

            return validateCoreRules(accountId, jti, tokenType, issueTime, expectedTokenType);
        } catch (ParseException e) {
            log.error("Failed to parse claims while validating business rules", e);
            return false;
        }
    }

    public Map<TokenType, String> refreshToken(Account account, String refreshTokenStr) {
        try {
            SignedJWT validToken = verifyToken(refreshTokenStr, TokenType.REFRESH);
            JWTClaimsSet claimsSet = validToken.getJWTClaimsSet();

            String jti = claimsSet.getJWTID();
            Date expirationTime = claimsSet.getExpirationTime();

            long timeToLiveMillis = expirationTime.getTime() - System.currentTimeMillis();
            if (timeToLiveMillis > 0) {
                revokeToken(jti, timeToLiveMillis);
            }

            Map<TokenType, String> tokens = new EnumMap<>(TokenType.class);
            tokens.put(TokenType.ACCESS, generateToken(account, TokenType.ACCESS));
            tokens.put(TokenType.REFRESH, generateToken(account, TokenType.REFRESH));

            return tokens;

        } catch (ParseException e) {
            throw new AppException(CodeException.TOKEN_INVALID);
        }
    }

    public void revokeToken(String jti, Long timeoutMillis) {
        redisService.set(BLACKLIST_PREFIX + jti, "revoked", timeoutMillis, TimeUnit.MILLISECONDS);
    }

    public void revokeToken(SignedJWT signedJWT) {
        try {
            JWTClaimsSet claimsSet = signedJWT.getJWTClaimsSet();

            Date expirationTime = claimsSet.getExpirationTime();
            long timeToLiveMillis = expirationTime.getTime() - System.currentTimeMillis();
            if (timeToLiveMillis > 0) {
                revokeToken(claimsSet.getJWTID(), timeToLiveMillis);
            }

        } catch (ParseException e) {
            log.warn("Lỗi parse token: {}", e.getMessage());
            throw new AppException(CodeException.TOKEN_INVALID);
        }
    }

    public void lockSession(String accountId) {
        redisService.set(
                BANNED_SESSION_PREFIX + accountId,
                String.valueOf(Instant.now().toEpochMilli()),
                REFRESH_TOKEN_VALIDITY,
                TimeUnit.SECONDS
        );
    }

    private String buildScope(Account account) {
        Set<String> permissions = permissionRepository.findAllPermissionNamesByAccountId(account.getId());

        if (CollectionUtils.isEmpty(permissions)) {
            return "";
        }

        // Output example: "*:*:* identity:account:read order:*:write"
        return String.join(" ", permissions);
    }

    public enum TokenType {
        ACCESS, REFRESH;

        boolean match(String type) {
            return this.name().equalsIgnoreCase(type);
        }
    }
}