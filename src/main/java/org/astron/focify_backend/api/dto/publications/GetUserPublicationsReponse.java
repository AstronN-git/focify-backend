package org.astron.focify_backend.api.dto.publications;

import lombok.Builder;
import lombok.Data;
import org.astron.focify_backend.api.dto.entity.PublicationDto;

import java.util.List;

@Data
@Builder
public class GetUserPublicationsReponse {
    private String error;
    private List<PublicationDto> publications;
}
