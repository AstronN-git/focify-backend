package org.astron.focify_backend.api.controller;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.extern.slf4j.Slf4j;
import org.astron.focify_backend.api.dto.PublishSessionRequest;
import org.astron.focify_backend.api.dto.PublishSessionResponse;
import org.astron.focify_backend.api.entity.Publication;
import org.astron.focify_backend.api.entity.User;
import org.astron.focify_backend.api.exception.AuthenticationException;
import org.astron.focify_backend.api.repository.PublicationRepository;
import org.astron.focify_backend.api.service.AuthenticationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Set;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/publications")
public class PublicationsController {
    private final AuthenticationService authenticationService;
    private final PublicationRepository publicationRepository;
    private final Validator validator;

    public PublicationsController(AuthenticationService authenticationService, PublicationRepository publicationRepository, Validator validator) {
        this.authenticationService = authenticationService;
        this.publicationRepository = publicationRepository;
        this.validator = validator;
    }

    @PostMapping("/publishSession")
    ResponseEntity<PublishSessionResponse> publishSession(@RequestBody PublishSessionRequest publishSessionRequest) {
        Set<ConstraintViolation<PublishSessionRequest>> violations = validator.validate(publishSessionRequest);
        List<String> errors = violations.stream().map(ConstraintViolation::getMessage).toList();

        if (!errors.isEmpty()) {
            PublishSessionResponse response = PublishSessionResponse.builder().error(errors.getFirst()).build();
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        String token = publishSessionRequest.getToken();

        User currentUser;

        try {
            currentUser = authenticationService.processToken(token);
        } catch (AuthenticationException exception) {
            PublishSessionResponse response = PublishSessionResponse.builder().error(exception.getMessage()).build();
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }


        Publication publication = new Publication();
        publication.setUser(currentUser);
        publication.setDuration(publishSessionRequest.getDuration());
        publication.setDescription(publishSessionRequest.getDescription());

        log.debug("Publish request with user {} and publication {}", currentUser, publication);

        publicationRepository.save(publication);

        return ResponseEntity.ok().build();
    }
}
