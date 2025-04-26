package org.astron.focify_backend.api.service;

import org.astron.focify_backend.api.entity.User;
import org.astron.focify_backend.api.exception.UsernameNotFoundException;
import org.astron.focify_backend.api.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class UserService {
    private final UserRepository userRepository;

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    public Optional<User> findByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public void addFriend(User user, String friendUsername) {
        User friendUser = findByUsername(friendUsername).orElseThrow(() -> new UsernameNotFoundException("Friend's username not found"));
        user.getFriends().add(friendUser);

        userRepository.save(user);
    }
}
