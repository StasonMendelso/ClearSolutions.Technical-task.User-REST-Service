package com.stanislav.hlova.userrestservice.exception.response;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

@Data
@Builder
@AllArgsConstructor
public class ValidationErrorResponse {
    @JsonInclude(JsonInclude.Include.NON_NULL)
    private String fieldName;
    private String errorMessage;
    private Object passedValue;
}
