package org.astron.focify_backend.api.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SignupResponse {
    private String token;
    private String error;
}
