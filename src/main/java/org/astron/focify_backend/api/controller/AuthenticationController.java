package org.astron.focify_backend.api.controller;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import org.astron.focify_backend.api.dto.auth.AuthenticationRequest;
import org.astron.focify_backend.api.dto.auth.AuthenticationResponse;
import org.astron.focify_backend.api.dto.auth.SignupRequest;
import org.astron.focify_backend.api.dto.auth.SignupResponse;
import org.astron.focify_backend.api.exception.AuthenticationException;
import org.astron.focify_backend.api.exception.SignupException;
import org.astron.focify_backend.api.service.AuthenticationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {
    private final AuthenticationService authenticationService;
    private final Validator validator;

    public AuthenticationController(AuthenticationService authenticationService, Validator validator) {
        this.authenticationService = authenticationService;
        this.validator = validator;
    }

    @PostMapping("/authenticate")
    ResponseEntity<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest authenticationRequest) {
        Set<ConstraintViolation<AuthenticationRequest>> constraintViolations = validator.validate(authenticationRequest);
        List<String> errors = constraintViolations.stream().map(ConstraintViolation::getMessage).toList();

        if (!errors.isEmpty()) {
            return new ResponseEntity<>(AuthenticationResponse.builder().error(errors.getFirst()).build(), HttpStatus.BAD_REQUEST);
        }

        try {
            String token = authenticationService.login(authenticationRequest.getUsername(), authenticationRequest.getPassword());
            AuthenticationResponse response = AuthenticationResponse.builder().token(token).build();

            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (AuthenticationException exception) {
            AuthenticationResponse response = AuthenticationResponse.builder().error("Incorrect username/password").build();
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }

    @PostMapping("/signup")
    ResponseEntity<SignupResponse> signup(@RequestBody SignupRequest signupRequest) {
        Set<ConstraintViolation<SignupRequest>> constraintViolations = validator.validate(signupRequest);
        List<String> errors = constraintViolations.stream().map(ConstraintViolation::getMessage).toList();

        if (!errors.isEmpty()) {
            return new ResponseEntity<>(SignupResponse.builder().error(errors.getFirst()).build(), HttpStatus.BAD_REQUEST);
        }

        try {
            String token = authenticationService.signup(signupRequest.getEmail(), signupRequest.getUsername(), signupRequest.getPassword());
            SignupResponse response = SignupResponse.builder().token(token).build();

            return new ResponseEntity<>(response, HttpStatus.OK);
        } catch (SignupException exception) {
            SignupResponse response = SignupResponse.builder().error(exception.getMessage()).build();
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }
    }
}
