package com.sigmaproject1.project1.service;

import com.sigmaproject1.project1.config.properties.XsrfProperties;

import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.util.Base64;
import java.util.UUID;

import static java.nio.charset.StandardCharsets.UTF_8;

@Service
public class XsrfTokenService {
    private final String alg = "HmacSHA256";
    private final Key key;
    private final Base64.Encoder encoder = Base64.getUrlEncoder().withoutPadding();

    public XsrfTokenService(final XsrfProperties xsrfProperties) {
        byte[] secretBytes = Base64.getDecoder().decode(xsrfProperties.secret());

        if(secretBytes == null || secretBytes.length <32)
            throw new IllegalStateException("XRSF secret length must be >= 32 bytes");

        this.key = new SecretKeySpec(secretBytes, alg);

    }

    // output: "random.signature"
    public String generateSigned(final UUID refreshTokenFamilyId) {
        String random = UUID.randomUUID().toString();
        return random + "." + sign(refreshTokenFamilyId.toString(), random);
    }


    // output: "family_id.random" encoded by secret
    @SneakyThrows
    private String sign(final String refreshTokenFamilyId, final String random) {
        Mac mac = Mac.getInstance(alg);
        mac.init(key);
        return encoder.encodeToString(mac.doFinal(
                (refreshTokenFamilyId + "." + random).getBytes(UTF_8)
        ));
    }
}
