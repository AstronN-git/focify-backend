package org.astron.focify_backend.api.controller;

import org.astron.focify_backend.api.dto.AuthenticationRequest;
import org.astron.focify_backend.api.dto.AuthenticationResponse;
import org.astron.focify_backend.api.exception.AuthenticationException;
import org.astron.focify_backend.api.service.AuthenticationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/auth")
public class AuthenticationController {
    private final AuthenticationService authenticationService;

    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping("/authenticate")
    ResponseEntity<AuthenticationResponse> authenticate(@RequestBody AuthenticationRequest authenticationRequest) {
        if (authenticationRequest.getUsername() == null || authenticationRequest.getPassword() == null) {
            AuthenticationResponse response = AuthenticationResponse.builder().error("Username or password not present").build();
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
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
}
