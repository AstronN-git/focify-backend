package org.astron.focify_backend.api.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class PostSessionRequest {
    private String token;
    private int focusTime;
}