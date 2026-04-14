package com.dev.minn.identity.controller;

import com.dev.minn.identity.config.RsaKeyConfig;
import com.nimbusds.jose.jwk.JWKSet;
import com.nimbusds.jose.jwk.RSAKey;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.interfaces.RSAPublicKey;
import java.util.Map;
import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/jwt")
@RequiredArgsConstructor
@FieldDefaults(level = lombok.AccessLevel.PRIVATE, makeFinal = true)
public class JwtController {

    RsaKeyConfig rsaKeys;

    @GetMapping("/.well-known/jwks.json")
    public Map<String, Object> keys() {
        log.warn("Public key requested");
        // Build chuẩn RSAKey của Nimbus từ java.security.interfaces.RSAPublicKey
        RSAKey rsaKey = new RSAKey.Builder((RSAPublicKey) rsaKeys.publicKey())
                // keyID (kid) cực kỳ quan trọng nếu sau này bạn muốn đổi key (Key Rotation)
                // Nó giúp API Gateway biết đang dùng key nào để verify
                .keyID(UUID.nameUUIDFromBytes(rsaKeys.publicKey().getEncoded()).toString())
                .build();

        // Đóng gói vào JWKSet (Set vì một hệ thống có thể có nhiều Public Key cùng lúc)
        JWKSet jwkSet = new JWKSet(rsaKey);

        // Trả về định dạng JSON chuẩn
        return jwkSet.toJSONObject();
    }
}
