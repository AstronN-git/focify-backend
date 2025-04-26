package org.astron.focify_backend.api.dto.friends;

import lombok.Builder;
import lombok.Data;
import org.astron.focify_backend.api.dto.entity.UserDto;

import java.util.List;

@Data
@Builder
public class GetFriendsResponse {
    private String error;
    private List<UserDto> friends;
}
