package com.stanislav.hlova.userrestservice.exception.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class MismatchErrorResponse {
    private String fieldName;
    private String errorMessage;
}
