package org.astron.focify_backend.api.controller;

import org.astron.focify_backend.api.dto.AddFriendRequest;
import org.astron.focify_backend.api.dto.GetFriendsResponse;
import org.astron.focify_backend.api.dto.UserDto;
import org.astron.focify_backend.api.entity.User;
import org.astron.focify_backend.api.exception.AuthenticationException;
import org.astron.focify_backend.api.exception.UsernameNotFoundException;
import org.astron.focify_backend.api.service.AuthenticationService;
import org.astron.focify_backend.api.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/friends")
public class FriendsController {
    private final AuthenticationService authenticationService;
    private final UserService userService;

    public FriendsController(AuthenticationService authenticationService, UserService userService) {
        this.authenticationService = authenticationService;
        this.userService = userService;
    }

    @PostMapping("/addFriend")
    public ResponseEntity<Void> addFriend(
            @RequestHeader("Authorization") String authorizationToken,
            @RequestBody AddFriendRequest addFriendRequest
    ) {
        User currentUser;

        try {
            currentUser = authenticationService.processToken(authorizationToken);
        } catch (AuthenticationException exception) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }

        try {
            userService.addFriend(currentUser, addFriendRequest.getUsername());
        } catch (UsernameNotFoundException exception) {
            return ResponseEntity.badRequest().build();
        }

        return ResponseEntity.ok().build();
    }

    @GetMapping("/allFriends")
    public ResponseEntity<GetFriendsResponse> getFriends(
            @RequestHeader("Authorization") String authorizationToken
    ) {
        User currentUser;

        try {
            currentUser = authenticationService.processToken(authorizationToken);
        } catch (AuthenticationException exception) {
            return new ResponseEntity<>(GetFriendsResponse.builder().error(exception.getMessage()).build(), HttpStatus.UNAUTHORIZED);
        }

        var friends = currentUser.getFriends().stream().map(UserDto::new).toList();

        return new ResponseEntity<>(GetFriendsResponse.builder().friends(friends).build(), HttpStatus.OK);
    }
}
