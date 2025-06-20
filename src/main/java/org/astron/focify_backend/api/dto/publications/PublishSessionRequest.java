package org.astron.focify_backend.api.dto.publications;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.validator.constraints.Length;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PublishSessionRequest {
    @NotNull(message = "Duration must not be null")
    private Long duration;

    @NotNull(message = "Description must not be null")
    @Length(max = 500, message = "Description is too long")
    private String description;

    private Date createdAt;
}