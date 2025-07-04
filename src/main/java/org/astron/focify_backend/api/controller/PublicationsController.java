package org.astron.focify_backend.api.controller;

import jakarta.validation.ConstraintViolation;
import jakarta.validation.Validator;
import lombok.extern.slf4j.Slf4j;
import org.astron.focify_backend.api.dto.entity.PublicationDto;
import org.astron.focify_backend.api.dto.publications.GetUserPublicationsReponse;
import org.astron.focify_backend.api.dto.publications.PublishSessionRequest;
import org.astron.focify_backend.api.dto.publications.PublishSessionResponse;
import org.astron.focify_backend.api.entity.Publication;
import org.astron.focify_backend.api.entity.User;
import org.astron.focify_backend.api.repository.PublicationRepository;
import org.astron.focify_backend.api.service.AuthenticationService;
import org.astron.focify_backend.api.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
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
    private final UserService userService;

    public PublicationsController(AuthenticationService authenticationService, PublicationRepository publicationRepository, Validator validator, UserService userService) {
        this.authenticationService = authenticationService;
        this.publicationRepository = publicationRepository;
        this.validator = validator;
        this.userService = userService;
    }

    @PostMapping("/publishSession")
    ResponseEntity<PublishSessionResponse> publishSession(
            @RequestHeader("Authorization") String authorizationToken,
            @RequestBody PublishSessionRequest publishSessionRequest
    ) {
        Set<ConstraintViolation<PublishSessionRequest>> violations = validator.validate(publishSessionRequest);
        List<String> errors = violations.stream().map(ConstraintViolation::getMessage).toList();

        if (!errors.isEmpty()) {
            PublishSessionResponse response = PublishSessionResponse.builder().error(errors.getFirst()).build();
            return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
        }

        User currentUser = authenticationService.processToken(authorizationToken);

        Publication publication = new Publication();
        publication.setAuthor(currentUser);
        publication.setDuration(publishSessionRequest.getDuration());
        publication.setDescription(publishSessionRequest.getDescription());

        if (publishSessionRequest.getCreatedAt() != null) {
            publication.setCreatedAt(publishSessionRequest.getCreatedAt());
        } else {
            publication.setCreatedAt(new Date());
        }

        log.debug("Publish request with user {} and publication {}", currentUser, publication);

        publicationRepository.save(publication);

        return ResponseEntity.ok().build();
    }

    @GetMapping("/userPublications")
    ResponseEntity<GetUserPublicationsReponse> getUserPublications(
            @RequestHeader("Authorization") String authorizationToken,
            @RequestParam(name = "author", required = false) String authorParam
    ) {
        User currentUser = authenticationService.processToken(authorizationToken);

        User author;

        if (authorParam != null) {
            Optional<User> authorOptional = userService.findByUsername(authorParam);

            if (authorOptional.isEmpty()) {
                GetUserPublicationsReponse response = GetUserPublicationsReponse.builder().error("Invalid author").build();
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }

            author = authorOptional.get();
        } else {
            author = currentUser;
        }

        List<Publication> publications = publicationRepository.findByAuthor(author);
        List<PublicationDto> publicationDtoList = publications.stream().map(PublicationDto::new).toList();

        GetUserPublicationsReponse response = GetUserPublicationsReponse.builder().publications(publicationDtoList).build();
        return new ResponseEntity<>(response, HttpStatus.OK);
    }
}
