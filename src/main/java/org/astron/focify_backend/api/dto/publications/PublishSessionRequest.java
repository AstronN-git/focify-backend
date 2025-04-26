package org.astron.focify_backend.api.dto.publications;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
@AllArgsConstructor
public class PublishSessionRequest {
    @NotNull(message = "Duration must not be null")
    private long duration;

    @NotNull(message = "Description must not be null")
    @Length(max = 500, message = "Description is too long")
    private String description;
}