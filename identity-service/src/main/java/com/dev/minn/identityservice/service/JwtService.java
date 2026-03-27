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

            boolean isBusinessValid = validateBusinessRules(jti, tokenTypeStr, expectedTokenType);

            if (!isBusinessValid) {
                throw new AppException(CodeException.TOKEN_INVALID);
            }

            return signedJWT;
        } catch (ParseException | JOSEException e) {
            log.warn("Lỗi parse hoặc verify token: {}", e.getMessage());
            throw new AppException(CodeException.TOKEN_INVALID);
        }
    }

    public boolean validateBusinessRules(String jti, String tokenTypeStr, TokenType expectedTokenType) {
        boolean isMatchType = expectedTokenType.match(tokenTypeStr);
        boolean isBlacklisted = redisService.exists(BLACKLIST_PREFIX + jti);

        return isMatchType && !isBlacklisted;
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