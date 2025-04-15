package org.astron.focify_backend.api.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class GetUserPublicationsRequest {
    @NotNull
    private String token;
    private String author;
}
