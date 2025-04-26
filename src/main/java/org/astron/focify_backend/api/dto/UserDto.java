package org.astron.focify_backend.api.dto;

import lombok.Data;
import org.astron.focify_backend.api.entity.User;

@Data
public class UserDto {
    private String username;

    public UserDto(User user) {
        username = user.getUsername();
    }
}
