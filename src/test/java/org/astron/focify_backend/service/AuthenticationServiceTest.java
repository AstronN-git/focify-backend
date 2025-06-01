package org.astron.focify_backend.service;

import at.favre.lib.crypto.bcrypt.BCrypt;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.assertj.core.data.Offset;
import org.astron.focify_backend.api.entity.User;
import org.astron.focify_backend.api.exception.AuthenticationException;
import org.astron.focify_backend.api.exception.UsernameNotFoundException;
import org.astron.focify_backend.api.repository.UserRepository;
import org.astron.focify_backend.api.service.AuthenticationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;

import javax.crypto.SecretKey;
import java.security.Key;
import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.when;

@WebMvcTest(AuthenticationService.class)
public class AuthenticationServiceTest {
    @Autowired
    private AuthenticationService authenticationService;

    @MockitoBean
    private UserRepository userRepository;

    @Value("${JWT_KEY}")
    private String jwtKey;

    @Test
    void testLoginThrowsExceptionOnUsernameNotFound() {
        when(userRepository.findByUsername("username")).thenReturn(Optional.empty());

        assertThatException().isThrownBy(() -> authenticationService.login("username", "password"))
                .isInstanceOf(AuthenticationException.class);
    }

    @Test
    void testLoginThrowsExceptionOnWrongPassword() {
        User user = new User();
        user.setUsername("username");

        String passwordEncrypted = BCrypt.withDefaults().hashToString(12, "password".toCharArray());

        user.setPassword(passwordEncrypted);
        when(userRepository.findByUsername("username")).thenReturn(Optional.of(user));

        assertThatException().isThrownBy(() -> authenticationService.login("username", "wrongPassword"))
                .isInstanceOf(AuthenticationException.class);
    }

    @Test
    void testLoginReturnsValidJwtToken() {
        User user = new User();
        user.setUsername("username");

        String passwordEncrypted = BCrypt.withDefaults().hashToString(12, "password".toCharArray());

        user.setPassword(passwordEncrypted);
        when(userRepository.findByUsername("username")).thenReturn(Optional.of(user));

        String token = authenticationService.login("username", "password");

        SecretKey key = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtKey));

        JwtParser jwtParser = Jwts.parser().verifyWith(key).build();

        Claims claims;

        assertThatNoException().isThrownBy(() -> jwtParser.parseSignedClaims(token));

        claims = jwtParser.parseSignedClaims(token).getPayload();

        assertThat(claims.getSubject()).isEqualTo("username");
        assertThat(claims.getIssuedAt()).isCloseTo(Instant.now(), 5000);
    }
}
