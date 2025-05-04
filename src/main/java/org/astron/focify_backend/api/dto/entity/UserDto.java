package org.astron.focify_backend.api.dto.entity;

import org.astron.focify_backend.api.entity.User;

public record UserDto(String username) {
    public UserDto(User user) {
        this(
                user.getUsername()
        );
    }
}
