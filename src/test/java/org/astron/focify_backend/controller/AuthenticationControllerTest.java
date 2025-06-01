package org.astron.focify_backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.astron.focify_backend.api.controller.AuthenticationController;
import org.astron.focify_backend.api.dto.auth.AuthenticationRequest;
import org.astron.focify_backend.api.dto.auth.SignupRequest;
import org.astron.focify_backend.api.exception.AuthenticationException;
import org.astron.focify_backend.api.exception.SignupException;
import org.astron.focify_backend.api.service.AuthenticationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(AuthenticationController.class)
public class AuthenticationControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private AuthenticationService authenticationService;

    private final ObjectMapper objectMapper = new ObjectMapper();
    private final String jwt = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJhd2Vzb21lVXNlciIsImlhdCI6MTUxNjIzOTAyMn0.duuzJafYuwq8dGiiy8PHaYq47bDP6eby8EcJmIni4aU";

    @Test
    void testAuthenticateValidationOnNullFields() throws Exception {
        AuthenticationRequest authenticationRequest = new AuthenticationRequest();
        authenticationRequest.setPassword("strongPassword12345!!");

        String requestBody = objectMapper.writeValueAsString(authenticationRequest);

        mockMvc.perform(post("/api/auth/authenticate").contentType("application/json").content(requestBody))
                .andExpect(status().isBadRequest());

        authenticationRequest.setUsername("awesomeUsername");
        authenticationRequest.setPassword(null);

        requestBody = objectMapper.writeValueAsString(authenticationRequest);

        mockMvc.perform(post("/api/auth/authenticate").contentType("application/json").content(requestBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testAuthenticateSuccessReturnsToken() throws Exception {
        AuthenticationRequest authenticationRequest = new AuthenticationRequest();

        authenticationRequest.setUsername("awesomeUser");
        authenticationRequest.setPassword("strongPassword12345!!");

        String requestBody = objectMapper.writeValueAsString(authenticationRequest);

        when(authenticationService.login("awesomeUser", "strongPassword12345!!")).thenReturn(jwt);

        mockMvc.perform(post("/api/auth/authenticate").contentType("application/json").content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("token").value(jwt));

        verify(authenticationService).login("awesomeUser", "strongPassword12345!!");
    }

    @Test
    void testAuthenticateReturnsUnauthorizedOnAuthenticationException() throws Exception {
        AuthenticationRequest authenticationRequest = new AuthenticationRequest();

        authenticationRequest.setUsername("awesomeUser");
        authenticationRequest.setPassword("strongPassword12345!!");

        String requestBody = objectMapper.writeValueAsString(authenticationRequest);

        when(authenticationService.login("awesomeUser", "strongPassword12345!!"))
                .thenThrow(new AuthenticationException("Invalid username/password"));

        mockMvc.perform(post("/api/auth/authenticate").contentType("application/json").content(requestBody))
                .andExpect(status().isUnauthorized());

        verify(authenticationService).login("awesomeUser", "strongPassword12345!!");
    }

    @Test
    void testSignupValidationOnNullFields() throws Exception {
        SignupRequest request = new SignupRequest();
        request.setEmail("example@mail.org");
        request.setPassword("strongPassword12345!!");

        String requestBody = objectMapper.writeValueAsString(request);

        mockMvc.perform(post("/api/auth/signup").contentType("application/json").content(requestBody))
                .andExpect(status().isBadRequest());

        request.setUsername("awesomeUser");
        request.setPassword(null);

        requestBody = objectMapper.writeValueAsString(request);

        mockMvc.perform(post("/api/auth/signup").contentType("application/json").content(requestBody))
                .andExpect(status().isBadRequest());

        request.setEmail(null);
        request.setPassword("strongPassword12345!!");

        requestBody = objectMapper.writeValueAsString(request);

        mockMvc.perform(post("/api/auth/signup").contentType("application/json").content(requestBody))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testSignupSuccessReturnsToken() throws Exception {
        SignupRequest request = new SignupRequest();
        request.setEmail("example@mail.org");
        request.setUsername("awesomeUser");
        request.setPassword("strongPassword12345!!");

        String requestBody = objectMapper.writeValueAsString(request);

        when(authenticationService.signup("example@mail.org", "awesomeUser", "strongPassword12345!!")).thenReturn(jwt);

        mockMvc.perform(post("/api/auth/signup").contentType("application/json").content(requestBody))
                .andExpect(status().isOk())
                .andExpect(jsonPath("token").value(jwt));

        verify(authenticationService).signup("example@mail.org", "awesomeUser", "strongPassword12345!!");
    }

    @Test
    void testSignupReturnsBadRequestOnSignupException() throws Exception {
        SignupRequest request = new SignupRequest();
        request.setEmail("example@mail.org");
        request.setUsername("awesomeUser");
        request.setPassword("strongPassword12345!!");

        String requestBody = objectMapper.writeValueAsString(request);

        when(authenticationService.signup(any(), any(), any())).thenThrow(new SignupException("Email is already taken"));

        mockMvc.perform(post("/api/auth/signup").contentType("application/json").content(requestBody))
                .andExpect(status().isBadRequest());

        verify(authenticationService).signup(any(), any(), any());
    }
}
