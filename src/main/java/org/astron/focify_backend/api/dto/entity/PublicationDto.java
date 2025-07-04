package org.astron.focify_backend.api.dto.entity;

import org.astron.focify_backend.api.entity.Publication;

import java.util.Date;

public record PublicationDto(Long author_id, String author, String description, Long duration, Date createdAt) {
    public PublicationDto(Publication publication) {
        this(
                publication.getAuthor().getId(),
                publication.getAuthor().getUsername(),
                publication.getDescription(),
                publication.getDuration(),
                publication.getCreatedAt()
        );
    }
}