package org.astron.focify_backend.api.service;

import at.favre.lib.crypto.bcrypt.BCrypt;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.astron.focify_backend.api.entity.User;
import org.astron.focify_backend.api.exception.AuthenticationException;
import org.astron.focify_backend.api.exception.InvalidTokenException;
import org.astron.focify_backend.api.exception.SignupException;
import org.astron.focify_backend.api.repository.UserRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Optional;

@Slf4j
@Service
public class AuthenticationService {
    private final SecretKey jwtKey;
    private final UserRepository userRepository;

    public AuthenticationService(@Value("${JWT_KEY}") String jwtKeyString, UserRepository userRepository) {
        jwtKey = Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtKeyString));
        this.userRepository = userRepository;
    }

    public String login(String username, String password) throws AuthenticationException {
        Optional<User> userOptional = userRepository.findByUsername(username);
        if (userOptional.isEmpty()) {
            throw new AuthenticationException("User not found");
        }

        User user = userOptional.get();

        if (!checkPasswordHash(password, user.getPassword())) {
            throw new AuthenticationException("Incorrect password");
        }

        String jws = Jwts
                .builder()
                .subject(username)
                .issuedAt(new Date())
                .signWith(jwtKey)
                .compact();

        log.debug("Created jwt {} for {}", jws, username);
        return jws;
    }

    public String signup(String email, String username, String password) throws SignupException {
        if (userRepository.existsByEmail(email)) {
            throw new SignupException("Email is already taken");
        }

        if (userRepository.existsByUsername(username)) {
            throw new SignupException("Username is already taken");
        }

        password = encryptPassword(password);

        User user = new User();
        user.setEmail(email);
        user.setUsername(username);
        user.setPassword(password);

        userRepository.save(user);

        String jws = Jwts
                .builder()
                .subject(username)
                .issuedAt(new Date())
                .signWith(jwtKey)
                .compact();

        log.debug("registered user {} and generated jws {}", user, jws);
        return jws;
    }

    public User processToken(String token) {
        if (!token.startsWith("Bearer ")) {
            throw new InvalidTokenException("Invalid token");
        }

        token = token.substring("Bearer ".length());

        var parser = Jwts.parser().verifyWith(jwtKey).build();

        Jws<Claims> jwt;

        try {
            jwt = parser.parseSignedClaims(token);
        } catch (JwtException exception) {
            throw new InvalidTokenException("Invalid token");
        }

        Claims claims = jwt.getPayload();
        String username = claims.getSubject();

        return userRepository.findByUsername(username).orElseThrow(() -> new InvalidTokenException("Invalid token"));
    }

    private String encryptPassword(String password) {
        return BCrypt.withDefaults().hashToString(12, password.toCharArray());
    }

    private boolean checkPasswordHash(String password, String hash) {
        return BCrypt.verifyer().verify(password.toCharArray(), hash.toCharArray()).verified;
    }
}
