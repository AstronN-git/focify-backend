package org.astron.focify_backend.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PublicationDto {
    private String author;
    private String description;
    private Long duration;
}