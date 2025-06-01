package org.astron.focify_backend;

import org.astron.focify_backend.api.controller.*;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class ContextTest {
    @Autowired
    private AuthenticationController authenticationController;

    @Autowired
    private FeedController feedController;

    @Autowired
    private FriendsController friendsController;

    @Autowired
    private PingController pingController;

    @Autowired
    private PublicationsController publicationsController;

    @Test
    void contextLoads() {
        assertThat(authenticationController).isNotNull();
        assertThat(feedController).isNotNull();
        assertThat(friendsController).isNotNull();
        assertThat(pingController).isNotNull();
        assertThat(publicationsController).isNotNull();
    }

}
