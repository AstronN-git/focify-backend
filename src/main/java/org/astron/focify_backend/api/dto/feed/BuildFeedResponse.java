package org.astron.focify_backend.api.dto.feed;

import lombok.Builder;
import lombok.Data;
import org.astron.focify_backend.api.dto.entity.PublicationDto;

import java.util.List;

@Data
@Builder
public class BuildFeedResponse {
    String error;
    List<PublicationDto> publications;
}
