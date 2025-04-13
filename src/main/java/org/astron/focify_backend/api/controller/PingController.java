package org.astron.focify_backend.api.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api")
public class PingController {
    @GetMapping("/ping")
    String ping() {
        log.debug("Serving ping request");
        return "ok";
    }
}
