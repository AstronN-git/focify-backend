package org.astron.focify_backend.api.controller;

import org.astron.focify_backend.api.dto.friends.AddFriendRequest;
import org.astron.focify_backend.api.dto.friends.GetFriendsResponse;
import org.astron.focify_backend.api.dto.entity.UserDto;
import org.astron.focify_backend.api.dto.friends.SearchUsersResponse;
import org.astron.focify_backend.api.entity.User;
import org.astron.focify_backend.api.exception.AuthenticationException;
import org.astron.focify_backend.api.exception.UsernameNotFoundException;
import org.astron.focify_backend.api.service.AuthenticationService;
import org.astron.focify_backend.api.service.UserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
        User currentUser = authenticationService.processToken(authorizationToken);

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
        User currentUser = authenticationService.processToken(authorizationToken);

        var friends = currentUser.getFriends().stream().map(UserDto::new).toList();

        return new ResponseEntity<>(GetFriendsResponse.builder().friends(friends).build(), HttpStatus.OK);
    }

    @GetMapping("/searchUsers")
    public ResponseEntity<SearchUsersResponse> searchUsers(
            @RequestHeader("Authorization") String authorizationToken,
            @RequestParam("sample") String usernameSample
    ) {
        authenticationService.processToken(authorizationToken);

        var users = userService.findByUsernameSubstring(usernameSample).stream().map(UserDto::new).toList();

        return new ResponseEntity<>(SearchUsersResponse.builder().users(users).build(), HttpStatus.OK);
    }
}
