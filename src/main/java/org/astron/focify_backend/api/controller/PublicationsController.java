package org.astron.focify_backend.api.controller;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.extern.slf4j.Slf4j;
import org.astron.focify_backend.api.dto.*;
import org.astron.focify_backend.api.entity.Publication;
import org.astron.focify_backend.api.entity.User;
import org.astron.focify_backend.api.exception.AuthenticationException;
import org.astron.focify_backend.api.repository.PublicationRepository;
import org.astron.focify_backend.api.repository.UserRepository;
import org.astron.focify_backend.api.service.AuthenticationService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.Set;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/publications")
public class PublicationsController {
    private final AuthenticationService authenticationService;
    private final PublicationRepository publicationRepository;
    private final Validator validator;
    private final UserRepository userRepository;

    public PublicationsController(AuthenticationService authenticationService, PublicationRepository publicationRepository, Validator validator, UserRepository userRepository) {
        this.authenticationService = authenticationService;
        this.publicationRepository = publicationRepository;
        this.validator = validator;
        this.userRepository = userRepository;
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
        publication.setAuthor(currentUser);
        publication.setDuration(publishSessionRequest.getDuration());
        publication.setDescription(publishSessionRequest.getDescription());

        log.debug("Publish request with user {} and publication {}", currentUser, publication);

        publicationRepository.save(publication);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/userPublications")
    ResponseEntity<GetUserPublicationsReponse> getUserPublications(@RequestBody GetUserPublicationsRequest getUserPublicationsRequest) {
        Set<ConstraintViolation<GetUserPublicationsRequest>> constraintViolations = validator.validate(getUserPublicationsRequest);
        List<String> errors = constraintViolations.stream().map(ConstraintViolation::getMessage).toList();

        if (!errors.isEmpty()) {
            GetUserPublicationsReponse response = GetUserPublicationsReponse.builder().error(errors.getFirst()).build();
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        User currentUser;

        try {
            currentUser = authenticationService.processToken(getUserPublicationsRequest.getToken());
        } catch (AuthenticationException exception) {
            GetUserPublicationsReponse response = GetUserPublicationsReponse.builder().error(exception.getMessage()).build();
            return new ResponseEntity<>(response, HttpStatus.UNAUTHORIZED);
        }

        String authorUsername = Optional.ofNullable(getUserPublicationsRequest.getAuthor()).orElse(currentUser.getUsername());

        Optional<User> authorOptional = userRepository.findByUsername(authorUsername);

        if (authorOptional.isEmpty()) {
            GetUserPublicationsReponse response = GetUserPublicationsReponse.builder().error("Invalid author").build();
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        User author = authorOptional.get();

        List<Publication> publications = publicationRepository.findByAuthor(author);
        List<PublicationDto> publicationDtoList = publications.stream().map(it -> new PublicationDto(authorUsername, it.getDescription(), it.getDuration())).toList();

        GetUserPublicationsReponse response = GetUserPublicationsReponse.builder().publications(publicationDtoList).build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
