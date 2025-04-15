package org.astron.focify_backend.api.dto;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class GetUserPublicationsReponse {
    private String error;
    private List<PublicationDto> publications;
}
