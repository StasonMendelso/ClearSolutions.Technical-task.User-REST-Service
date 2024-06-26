package com.stanislav.hlova.userrestservice.exception.handler;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import com.fasterxml.jackson.databind.exc.MismatchedInputException;
import com.stanislav.hlova.userrestservice.exception.UserNotFoundException;
import com.stanislav.hlova.userrestservice.exception.response.CustomErrorResponse;
import com.stanislav.hlova.userrestservice.exception.response.MismatchErrorResponse;
import com.stanislav.hlova.userrestservice.exception.response.ValidationErrorResponse;
import org.springframework.core.convert.ConversionFailedException;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.LocalDateTime;
import java.util.Date;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

@ControllerAdvice
public class CustomHandlerExceptionResolver extends ResponseEntityExceptionHandler {

    public static final String DETAIL = "detail";

    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        Map<String, Object> body = new LinkedHashMap<>();
        setCommonFields(status, (ServletWebRequest) request, body);

        List<ValidationErrorResponse> errors = ex.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(fieldError -> ValidationErrorResponse.builder()
                        .fieldName(fieldError.getField())
                        .errorMessage(fieldError.getDefaultMessage())
                        .passedValue(fieldError.getRejectedValue())
                        .build())
                .toList();

        body.put("details", errors);

        return new ResponseEntity<>(body, headers, status);
    }

    @Override
    protected ResponseEntity<Object> handleHttpMessageNotReadable(HttpMessageNotReadableException exception, HttpHeaders headers, HttpStatusCode status, WebRequest request) {
        if (exception.getCause() instanceof InvalidFormatException invalidFormatException) {
            Map<String, Object> body = new LinkedHashMap<>();
            setCommonFields(status, (ServletWebRequest) request, body);
            body.put(DETAIL, ValidationErrorResponse.builder()
                    .fieldName(invalidFormatException.getPath().get(0).getFieldName()) //can be only one error at a time
                    .errorMessage("Invalid format or type of passed value. Check type, value and format.")
                    .passedValue(invalidFormatException.getValue())
                    .build());

            return new ResponseEntity<>(body, headers, status);
        }
        if (exception.getCause() instanceof MismatchedInputException mismatchedInputException) {
            Map<String, Object> body = new LinkedHashMap<>();
            setCommonFields(status, (ServletWebRequest) request, body);
            if (!mismatchedInputException.getPath().isEmpty()) {
                body.put(DETAIL, MismatchErrorResponse.builder()
                        .fieldName(mismatchedInputException.getPath().get(0).getFieldName()) //can be only one error at a time
                        .errorMessage("Invalid type of passed value. Check type and format.")
                        .build());

                return new ResponseEntity<>(body, headers, status);
            }
        }
        if (exception.getCause() instanceof ConversionFailedException conversionFailedException) {
            Map<String, Object> body = new LinkedHashMap<>();
            setCommonFields(status, (ServletWebRequest) request, body);
            body.put(DETAIL, ValidationErrorResponse.builder()
                    .errorMessage("Invalid format or type of passed value. Check type, value and format.")
                    .passedValue(conversionFailedException.getValue())
                    .build());

            return new ResponseEntity<>(body, headers, status);
        }
        logger.warn("Unknown exception occurred in HttpMessageNotReadableException", exception);
        return super.handleHttpMessageNotReadable(exception, headers, status, request);
    }

    private void setCommonFields(HttpStatusCode status, ServletWebRequest request, Map<String, Object> body) {
        body.put("path", request.getRequest().getRequestURI());
        body.put("method", request.getRequest().getMethod());
        body.put("title", "Bad Request");
        body.put("timestamp", new Date());
        body.put("status", status.value());
    }

    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<CustomErrorResponse> handleUserNotFoundException(UserNotFoundException userNotFoundException) {
        CustomErrorResponse errorResponse = CustomErrorResponse.builder()
                .error(HttpStatus.NOT_FOUND.getReasonPhrase())
                .status(HttpStatus.NOT_FOUND.value())
                .timestamp(LocalDateTime.now())
                .message(String.format("User with id %s wasn't found", userNotFoundException.getUserId()))
                .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(errorResponse);
    }
}
