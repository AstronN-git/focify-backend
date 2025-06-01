package org.astron.focify_backend.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.astron.focify_backend.api.controller.PublicationsController;
import org.astron.focify_backend.api.dto.publications.PublishSessionRequest;
import org.astron.focify_backend.api.entity.Publication;
import org.astron.focify_backend.api.entity.User;
import org.astron.focify_backend.api.exception.AuthenticationException;
import org.astron.focify_backend.api.repository.PublicationRepository;
import org.astron.focify_backend.api.service.AuthenticationService;
import org.astron.focify_backend.api.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(PublicationsController.class)
public class PublicationsControllerTest {
    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private PublicationRepository publicationRepository;

    @MockitoBean
    private UserService userService;

    @MockitoBean
    private AuthenticationService authenticationService;

    private final String jwt = "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9.eyJzdWIiOiJhd2Vzb21lVXNlciIsImlhdCI6MTUxNjIzOTAyMn0.duuzJafYuwq8dGiiy8PHaYq47bDP6eby8EcJmIni4aU";
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void testPublishSessionReturnsUnauthorizedOnWrongToken() throws Exception {
        PublishSessionRequest publishSessionRequest = new PublishSessionRequest();
        publishSessionRequest.setDuration(1111L);
        publishSessionRequest.setDescription("description");

        String content = objectMapper.writeValueAsString(publishSessionRequest);

        when(authenticationService.processToken(any())).thenThrow(new AuthenticationException("invalid token"));

        mockMvc.perform(
                post("/api/publications/publishSession")
                        .header("Authorization", "Bearer invalid")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content)
        ).andExpect(status().isUnauthorized());

        verify(authenticationService).processToken(any());
    }

    @Test
    void testPublishSessionValidationOnNullFields() throws Exception {
        PublishSessionRequest publishSessionRequest = new PublishSessionRequest();
        publishSessionRequest.setDuration(1111L);

        String content = objectMapper.writeValueAsString(publishSessionRequest);

        mockMvc.perform(
                post("/api/publications/publishSession")
                        .header("Authorization", "Bearer " + jwt)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content)
        ).andExpect(status().isBadRequest());

        publishSessionRequest.setDuration(null);
        publishSessionRequest.setDescription("description");

        content = objectMapper.writeValueAsString(publishSessionRequest);

        mockMvc.perform(
                post("/api/publications/publishSession")
                        .header("Authorization", "Bearer " + jwt)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content)
        ).andExpect(status().isBadRequest());
    }

    @Test
    void testPublishSessionCreatesAndSavesPublication() throws Exception {
        PublishSessionRequest publishSessionRequest = new PublishSessionRequest();
        publishSessionRequest.setDuration(1111L);
        publishSessionRequest.setDescription("description");
        String content = objectMapper.writeValueAsString(publishSessionRequest);

        User user = new User();

        when(authenticationService.processToken("Bearer " + jwt)).thenReturn(user);
        when(publicationRepository.save(any())).then(invocation -> {
                    var publication = invocation.<Publication>getArgument(0);
                    assertThat(publication.getDuration()).isEqualTo(1111L);
                    assertThat(publication.getDescription()).isEqualTo("description");
                    assertThat(publication.getAuthor()).isEqualTo(user);
                    return publication;
                }
        );

        mockMvc.perform(
                post("/api/publications/publishSession")
                        .header("Authorization", "Bearer " + jwt)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(content)
        ).andExpect(status().isOk());

        verify(authenticationService).processToken("Bearer " + jwt);
        verify(publicationRepository, times(1)).save(any());
    }

    @Test
    void testUserPublicationsReturnsUnauthorizedOnWrongToken() throws Exception {
        when(authenticationService.processToken(any())).thenThrow(new AuthenticationException("invalid token"));

        mockMvc.perform(get("/api/publications/userPublications")
                .header("Authorization", "Bearer invalid")
        ).andExpect(status().isUnauthorized());

        verify(authenticationService).processToken(any());
    }

    @Test
    void testUserPublicationsBadRequestOnUsernameNotFound() throws Exception {
        User user = new User();

        when(authenticationService.processToken("Bearer " + jwt)).thenReturn(user);
        when(userService.findByUsername(anyString())).thenReturn(Optional.empty());

        mockMvc.perform(
                get("/api/publications/userPublications")
                        .header("Authorization", "Bearer " + jwt)
                        .queryParam("author", "username")
        ).andExpect(status().isBadRequest());

        verify(userService, times(1)).findByUsername(anyString());
    }

    @Test
    void testUserPublicationsWithoutAuthorParamReturnsCurrentUserPublications() throws Exception {
        User user = new User();
        user.setUsername("username");

        List<Publication> publications = new ArrayList<>();
        Publication publication = new Publication();
        publication.setAuthor(user);
        publications.add(publication);

        when(publicationRepository.findByAuthor(user)).thenReturn(publications);
        when(authenticationService.processToken("Bearer " + jwt)).thenReturn(user);

        mockMvc.perform(
                get("/api/publications/userPublications")
                        .header("Authorization", "Bearer " + jwt)
        )
                .andExpect(status().isOk())
                .andExpect(jsonPath("publications").isArray())
                .andExpect(jsonPath("publications[0].author").value(user.getUsername()));

        verify(publicationRepository, times(1)).findByAuthor(user);
    }

    @Test
    void testUserPublicationsWithAuthorParamReturnsCurrentUserPublications() throws Exception {
        User user = new User();
        user.setUsername("username");

        User author = new User();
        author.setUsername("author");

        List<Publication> publications = new ArrayList<>();
        Publication publication = new Publication();
        publication.setAuthor(author);
        publications.add(publication);

        when(userService.findByUsername("author")).thenReturn(Optional.of(author));
        when(publicationRepository.findByAuthor(author)).thenReturn(publications);
        when(authenticationService.processToken("Bearer " + jwt)).thenReturn(user);

        mockMvc.perform(
                        get("/api/publications/userPublications")
                                .header("Authorization", "Bearer " + jwt)
                                .queryParam("author", "author")
                )
                .andExpect(status().isOk())
                .andExpect(jsonPath("publications").isArray())
                .andExpect(jsonPath("publications[0].author").value(author.getUsername()));

        verify(userService, times(1)).findByUsername("author");
        verify(publicationRepository, times(1)).findByAuthor(author);
    }
}
