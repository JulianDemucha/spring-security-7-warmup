package com.sigmaproject1.project1.service;

import com.sigmaproject1.project1.config.properties.XsrfProperties;

import lombok.SneakyThrows;
import org.springframework.stereotype.Service;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;
import java.security.Key;
import java.security.MessageDigest;
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

    public boolean isValid(final String header, final String cookie, final String familyId) {
        if (header == null || cookie == null || familyId == null)
            return false;

        if (!MessageDigest.isEqual(header.getBytes(UTF_8), cookie.getBytes(UTF_8)))
            return false;

        int dot = header.indexOf(".");
        if (dot <= 0)
            return false;

        String uuid = header.substring(0, dot);

        String expected = sign(familyId, uuid);

        String signature = header.substring(dot + 1);

        return MessageDigest.isEqual(expected.getBytes(UTF_8), signature.getBytes(UTF_8));
    }
}
