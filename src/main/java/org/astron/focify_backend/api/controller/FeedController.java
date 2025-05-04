package org.astron.focify_backend.api.controller;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.astron.focify_backend.api.dto.entity.PublicationDto;
import org.astron.focify_backend.api.dto.feed.BuildFeedResponse;
import org.astron.focify_backend.api.entity.User;
import org.astron.focify_backend.api.exception.AuthenticationException;
import org.astron.focify_backend.api.repository.PublicationRepository;
import org.astron.focify_backend.api.service.AuthenticationService;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/feed")
public class FeedController {

    private final AuthenticationService authenticationService;
    private final PublicationRepository publicationRepository;

    public FeedController(AuthenticationService authenticationService, PublicationRepository publicationRepository) {
        this.authenticationService = authenticationService;
        this.publicationRepository = publicationRepository;
    }

    @GetMapping("/buildFeed")
    ResponseEntity<BuildFeedResponse> buildFeed(
            @RequestHeader("Authorization") String token,
            @RequestParam Integer page,
            @RequestParam @Min(1) @Max(50) Integer pageSize
    ) {
        User currentUser;

        try {
            currentUser = authenticationService.processToken(token);
        } catch (AuthenticationException exception) {
            return new ResponseEntity<>(BuildFeedResponse.builder().error(exception.getMessage()).build(), HttpStatus.UNAUTHORIZED);
        }

        Pageable pageable = PageRequest.of(page, pageSize);

        List<PublicationDto> publications =
                publicationRepository.buildFeed(currentUser, pageable)
                        .stream().map(PublicationDto::new).toList();

        return new ResponseEntity<>(BuildFeedResponse.builder().publications(publications).build(), HttpStatus.OK);
    }
}
