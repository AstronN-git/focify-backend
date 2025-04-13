package org.astron.focify_backend.api.controller;

import lombok.extern.slf4j.Slf4j;
import org.astron.focify_backend.api.dto.PostSessionRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/posts")
public class PostsController {
    @PostMapping("/postSession")
    ResponseEntity<Void> postSession(@RequestBody PostSessionRequest postSessionRequest) {
        log.debug("Post session request with token \"{}\" and time \"{}\".", postSessionRequest.getToken(), postSessionRequest.getFocusTime());
        return ResponseEntity.ok().build();
    }
}
