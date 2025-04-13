package org.astron.focify_backend.api.service;

import at.favre.lib.crypto.bcrypt.BCrypt;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.astron.focify_backend.api.exception.AuthenticationException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;

@Slf4j
@Service
public class AuthenticationService {
    private final SecretKey jwtKey;

    public AuthenticationService(@Value("${JWT_KEY}") String jwtKeyString)  {
        jwtKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtKeyString));
    }

    public String login(String username, String password) throws AuthenticationException {
        String jws = Jwts
                .builder()
                .subject(username)
                .signWith(jwtKey)
                .issuedAt(new Date())
                .compact();

        log.debug("Created jwt {} for {}", jws, username);
        return jws;
    }

    private String encryptPassword(String password) {
        return BCrypt.withDefaults().hashToString(12, password.toCharArray());
    }
}
