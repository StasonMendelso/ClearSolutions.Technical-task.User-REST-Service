package com.stanislav.hlova.userrestservice.controller;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.stanislav.hlova.userrestservice.dto.RegisterUserDto;
import com.stanislav.hlova.userrestservice.model.User;
import com.stanislav.hlova.userrestservice.service.UserService;
import com.stanislav.hlova.userrestservice.util.UriUtil;
import com.stanislav.hlova.userrestservice.validator.RegisterUserDtoValidator;
import org.apache.commons.io.IOUtils;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mockito.Mock;
import org.skyscreamer.jsonassert.JSONCompare;
import org.skyscreamer.jsonassert.JSONCompareMode;
import org.skyscreamer.jsonassert.JSONCompareResult;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.util.stream.Stream;

import static org.junit.jupiter.api.AssertionFailureBuilder.assertionFailure;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.header;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
@WebMvcTest({UserController.class, RegisterUserDtoValidator.class})
class UserControllerITWithValidation {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;

    @MockBean
    private UserService userService;
    @MockBean
    private UriUtil uriUtil;
    @Mock
    private User user;

    private static final String EMPTY_BODY = "{}";
    private static final String EXPECTED_VALIDATION_RESPONSE_WHEN_NOTHING_PASSED = "/userController/jsons/validationErrorResponse/validationErrorsWhenNothingPassed.json";
    private static final String EXPECTED_VALIDATION_RESPONSE_WHEN_REQUEST_NOT_PARSABLE = "/userController/jsons/validationErrorResponse/defaultValidationNotParsableRequest.json";
    private static final String EXPECTED_VALIDATION_RESPONSE_WHEN_INVALID_FIRST_NAME_TYPE = "/userController/jsons/validationErrorResponse/validationErrorsWhenPassedInvalidFirstNameType.json";
    private static final String EXPECTED_VALIDATION_RESPONSE_WHEN_INVALID_BIRTHDATE_TYPE = "/userController/jsons/validationErrorResponse/validationErrorsWhenPassedInvalidBirthdateType.json";
    private static final String EXPECTED_VALIDATION_RESPONSE_WHEN_USER_WITH_EMAIL_EXISTED = "/userController/jsons/validationErrorResponse/validationErrorsWhenUserWithEmailExists.json";
    private static final String EXPECTED_VALIDATION_RESPONSE_WHEN_BIRTHDAY_IN_FUTURE = "/userController/jsons/validationErrorResponse/validationErrorsWhenBirthdateInFuture.json";
    private static final String REQUEST_WITH_INVALID_FIRST_NAME_TYPE = "/userController/jsons/requests/requestWithInvalidFirstNameType.json";
    private static final String REQUEST_WITH_INVALID_BIRTHDATE_TYPE = "/userController/jsons/requests/requestWithInvalidBirthdateFormat.json";
    private static final String REQUEST_WITH_FUTURE_BIRTHDATE = "/userController/jsons/requests/requestWithBirthdateInFuture.json";
    private static final String REQUEST_WITH_UNEXPECTED_CODE = "/userController/jsons/requests/requestWithUnexpectedCode.json";
    private static final String REQUEST_WITH_VALID_VALUES = "/userController/jsons/requests/requestWithValidValues.json";


    @ParameterizedTest
    @MethodSource("provideRequestBodyAndExpectedValidationResponse")
    void shouldReturnValidationErrors_whenPassedInvalidValues(String requestBody, String expectedValidationResponse) throws Exception {
        MvcResult mvcResult = mockMvc.perform(post("/api/v1/users")
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn();

        String actualResponse = mvcResult.getResponse().getContentAsString();
        assertEqualIgnoringTimestamp(expectedValidationResponse, actualResponse);
    }

    public static Stream<Arguments> provideRequestBodyAndExpectedValidationResponse() throws IOException {
        return Stream.of(
                Arguments.of(EMPTY_BODY, readFile(EXPECTED_VALIDATION_RESPONSE_WHEN_NOTHING_PASSED)),
                Arguments.of(readFile(REQUEST_WITH_INVALID_FIRST_NAME_TYPE), readFile(EXPECTED_VALIDATION_RESPONSE_WHEN_INVALID_FIRST_NAME_TYPE)),
                Arguments.of(readFile(REQUEST_WITH_INVALID_BIRTHDATE_TYPE), readFile(EXPECTED_VALIDATION_RESPONSE_WHEN_INVALID_BIRTHDATE_TYPE)),
                Arguments.of(readFile(REQUEST_WITH_FUTURE_BIRTHDATE), readFile(EXPECTED_VALIDATION_RESPONSE_WHEN_BIRTHDAY_IN_FUTURE)),
                Arguments.of(readFile(REQUEST_WITH_UNEXPECTED_CODE), readFile(EXPECTED_VALIDATION_RESPONSE_WHEN_REQUEST_NOT_PARSABLE))
        );
    }

    @Test
    void shouldReturnValidationErrors_whenNoRequestBodyPassed() throws Exception {
        String expectedValidationResponse = readFile(EXPECTED_VALIDATION_RESPONSE_WHEN_REQUEST_NOT_PARSABLE);

        MvcResult mvcResult = mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn();

        String actualResponse = mvcResult.getResponse().getContentAsString();
        assertEqualIgnoringTimestamp(expectedValidationResponse, actualResponse);
    }

    @Test
    void shouldReturnCreated_whenPassedValidValues() throws Exception {
        String requestBody = readFile(REQUEST_WITH_VALID_VALUES);
        URI expectedLocation = new URI("/api/v1/users/1");
        when(userService.register(any(RegisterUserDto.class))).thenReturn(user);
        when(user.getId()).thenReturn(1L);
        when(uriUtil.createBaseUri(anyString())).thenReturn(expectedLocation);

        mockMvc.perform(post("/api/v1/users")
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(header().string(HttpHeaders.LOCATION, expectedLocation.toString()))
                .andExpect(status().isCreated());
    }

    @Test
    void shouldReturnValidationErrors_wheUserWithEmailExists() throws Exception {
        String requestBody = readFile(REQUEST_WITH_VALID_VALUES);
        String expectedValidationResponse = readFile(EXPECTED_VALIDATION_RESPONSE_WHEN_USER_WITH_EMAIL_EXISTED);
        when(userService.existByEmail("testuser@gmail.com")).thenReturn(true);

        MvcResult mvcResult = mockMvc.perform(post("/api/v1/users")
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn();

        String actualResponse = mvcResult.getResponse().getContentAsString();
        assertEqualIgnoringTimestamp(expectedValidationResponse, actualResponse);
    }

    private void assertEqualIgnoringTimestamp(String expectedResponse, String actualResponse) throws Exception {
        JsonNode expectedNode = objectMapper.readTree(expectedResponse);
        JsonNode actualNode = objectMapper.readTree(actualResponse);

        ((ObjectNode) expectedNode).remove("timestamp");
        ((ObjectNode) actualNode).remove("timestamp");


        JSONCompareResult result = JSONCompare.compareJSON(expectedNode.toString(), actualNode.toString(), JSONCompareMode.LENIENT);
        if (result.failed()) {
            assertionFailure()
                    .message("Actual response differs from expected.")
                    .expected(expectedNode.toPrettyString())
                    .actual(actualNode.toPrettyString())
                    .buildAndThrow();
        }
    }

    private static String readFile(String path) throws IOException {
        InputStream inputStream = new ClassPathResource(path).getInputStream();
        return IOUtils.toString(inputStream);
    }
}