package com.stanislav.hlova.userrestservice.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.stanislav.hlova.userrestservice.config.ValidatorConfig;
import com.stanislav.hlova.userrestservice.dto.ReadUserDto;
import com.stanislav.hlova.userrestservice.dto.RegisterUserDto;
import com.stanislav.hlova.userrestservice.dto.UpdateUserDto;
import com.stanislav.hlova.userrestservice.dto.UserBirthdateRangeQuery;
import com.stanislav.hlova.userrestservice.model.User;
import com.stanislav.hlova.userrestservice.service.UserService;
import com.stanislav.hlova.userrestservice.util.UriUtil;
import com.stanislav.hlova.userrestservice.validator.impl.RegisterUserValidator;
import com.stanislav.hlova.userrestservice.validator.impl.UpdateUserValidator;
import com.stanislav.hlova.userrestservice.validator.impl.UserBirthdateRangeQueryValidator;
import org.apache.commons.io.IOUtils;
import org.json.JSONException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.ArgumentMatchers;
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
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Stream;

import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.AssertionFailureBuilder.assertionFailure;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(value = {UserController.class, RegisterUserValidator.class, UserBirthdateRangeQueryValidator.class, UpdateUserValidator.class, ValidatorConfig.class})
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
    private static final String EXPECTED_POST_VALIDATION_RESPONSE_WHEN_REQUEST_NOT_PARSABLE = "/userController/jsons/responses/validationErrorResponse/post/defaultValidationNotParsableRequest.json";
    private static final String EXPECTED_POST_VALIDATION_RESPONSE_WHEN_NOTHING_PASSED = "/userController/jsons/responses/validationErrorResponse/post/validationErrorsWhenNothingPassed.json";
    private static final String EXPECTED_POST_VALIDATION_RESPONSE_WHEN_INVALID_FIRST_NAME_TYPE = "/userController/jsons/responses/validationErrorResponse/post/validationErrorsWhenPassedInvalidFirstNameType.json";
    private static final String EXPECTED_POST_VALIDATION_RESPONSE_WHEN_INVALID_BIRTHDATE_TYPE = "/userController/jsons/responses/validationErrorResponse/post/validationErrorsWhenPassedInvalidBirthdateType.json";
    private static final String EXPECTED_POST_VALIDATION_RESPONSE_WHEN_USER_WITH_EMAIL_EXISTED = "/userController/jsons/responses/validationErrorResponse/post/validationErrorsWhenUserWithEmailExists.json";
    private static final String EXPECTED_POST_VALIDATION_RESPONSE_WHEN_BIRTHDAY_IN_FUTURE = "/userController/jsons/responses/validationErrorResponse/post/validationErrorsWhenBirthdateInFuture.json";
    private static final String EXPECTED_PUT_VALIDATION_RESPONSE_WHEN_REQUEST_NOT_PARSABLE = "/userController/jsons/responses/validationErrorResponse/put/defaultValidationNotParsableRequest.json";
    private static final String EXPECTED_PUT_VALIDATION_RESPONSE_WHEN_NOTHING_PASSED = "/userController/jsons/responses/validationErrorResponse/put/validationErrorsWhenNothingPassed.json";
    private static final String EXPECTED_PUT_VALIDATION_RESPONSE_WHEN_INVALID_FIRST_NAME_TYPE = "/userController/jsons/responses/validationErrorResponse/put/validationErrorsWhenPassedInvalidFirstNameType.json";
    private static final String EXPECTED_PUT_VALIDATION_RESPONSE_WHEN_INVALID_BIRTHDATE_TYPE = "/userController/jsons/responses/validationErrorResponse/put/validationErrorsWhenPassedInvalidBirthdateType.json";
    private static final String EXPECTED_PUT_VALIDATION_RESPONSE_WHEN_USER_WITH_EMAIL_AND_USER_NOT_HAS_IT = "/userController/jsons/responses/validationErrorResponse/put/validationErrorsWhenUserWithEmailExists.json";
    private static final String EXPECTED_PUT_RESPONSE_WHEN_USER_WITH_EMAIL_AND_USER_HAS_IT = "/userController/jsons/responses/put/responseWithUpdatedUser.json";
    private static final String EXPECTED_PUT_VALIDATION_RESPONSE_WHEN_BIRTHDAY_IN_FUTURE = "/userController/jsons/responses/validationErrorResponse/put/validationErrorsWhenBirthdateInFuture.json";

    private static final String REQUEST_WITH_INVALID_FIRST_NAME_TYPE = "/userController/jsons/requests/requestWithInvalidFirstNameType.json";
    private static final String REQUEST_WITH_INVALID_BIRTHDATE_TYPE = "/userController/jsons/requests/requestWithInvalidBirthdateFormat.json";
    private static final String REQUEST_WITH_FUTURE_BIRTHDATE = "/userController/jsons/requests/requestWithBirthdateInFuture.json";
    private static final String REQUEST_WITH_UNEXPECTED_CODE = "/userController/jsons/requests/requestWithUnexpectedCode.json";
    private static final String REQUEST_WITH_VALID_VALUES = "/userController/jsons/requests/requestWithValidValues.json";
    private static final String REQUEST_WITH_VALID_VALUES_WITHOUT_OPTIONAL_FIELDS = "/userController/jsons/requests/requestWithValidValuesWithouOptionalFields.json";


    @ParameterizedTest
    @MethodSource("provideRequestBodyAndExpectedValidationResponse")
    void shouldReturnValidationErrors_whenPassedInvalidValuesForCreatingNewUser(String requestBody, String expectedValidationResponse) throws Exception {
        MvcResult mvcResult = mockMvc.perform(post("/api/v1/users")
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn();

        String actualResponse = mvcResult.getResponse().getContentAsString();
        assertEqualsIgnoringTimestamp(expectedValidationResponse, actualResponse);
    }

    public static Stream<Arguments> provideRequestBodyAndExpectedValidationResponse() throws IOException {
        return Stream.of(
                Arguments.of(EMPTY_BODY, readFile(EXPECTED_POST_VALIDATION_RESPONSE_WHEN_NOTHING_PASSED)),
                Arguments.of(readFile(REQUEST_WITH_INVALID_FIRST_NAME_TYPE), readFile(EXPECTED_POST_VALIDATION_RESPONSE_WHEN_INVALID_FIRST_NAME_TYPE)),
                Arguments.of(readFile(REQUEST_WITH_INVALID_BIRTHDATE_TYPE), readFile(EXPECTED_POST_VALIDATION_RESPONSE_WHEN_INVALID_BIRTHDATE_TYPE)),
                Arguments.of(readFile(REQUEST_WITH_FUTURE_BIRTHDATE), readFile(EXPECTED_POST_VALIDATION_RESPONSE_WHEN_BIRTHDAY_IN_FUTURE)),
                Arguments.of(readFile(REQUEST_WITH_UNEXPECTED_CODE), readFile(EXPECTED_POST_VALIDATION_RESPONSE_WHEN_REQUEST_NOT_PARSABLE))
        );
    }

    @Test
    void shouldReturnValidationErrors_whenNoRequestBodyPassed() throws Exception {
        String expectedValidationResponse = readFile(EXPECTED_POST_VALIDATION_RESPONSE_WHEN_REQUEST_NOT_PARSABLE);

        MvcResult mvcResult = mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn();

        String actualResponse = mvcResult.getResponse().getContentAsString();
        assertEqualsIgnoringTimestamp(expectedValidationResponse, actualResponse);
    }

    @ParameterizedTest
    @ValueSource(strings = {REQUEST_WITH_VALID_VALUES, REQUEST_WITH_VALID_VALUES_WITHOUT_OPTIONAL_FIELDS})
    void shouldReturnCreated_whenPassedValidValues(String filePath) throws Exception {
        String requestBody = readFile(filePath);
        URI expectedLocation = new URI("/api/v1/users/1");
        when(userService.register(ArgumentMatchers.any(RegisterUserDto.class))).thenReturn(user);
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
        String expectedValidationResponse = readFile(EXPECTED_POST_VALIDATION_RESPONSE_WHEN_USER_WITH_EMAIL_EXISTED);
        when(userService.existByEmail("testuser@gmail.com")).thenReturn(true);

        MvcResult mvcResult = mockMvc.perform(post("/api/v1/users")
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn();

        String actualResponse = mvcResult.getResponse().getContentAsString();
        assertEqualsIgnoringTimestamp(expectedValidationResponse, actualResponse);
    }

    @Test
    void shouldReturnBadRequest_whenNotPassParams() throws Exception {
        mockMvc.perform(get("/api/v1/users"))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.details[*].fieldName", containsInAnyOrder("birthdateFrom", "birthdateTo")))
                .andExpect(jsonPath("$.details[0].errorMessage", equalTo("Must be presented.")))
                .andExpect(jsonPath("$.details[1].errorMessage", equalTo("Must be presented.")))
                .andExpect(jsonPath("$.details[0].passedValue", nullValue(null)))
                .andExpect(jsonPath("$.details[1].passedValue", nullValue(null)));
    }

    @Test
    void shouldReturnBadRequest_whenFromGreaterToParam() throws Exception {
        mockMvc.perform(get("/api/v1/users")
                        .param("birthdateFrom", LocalDate.of(2020, 1, 1).toString())
                        .param("birthdateTo", LocalDate.of(1990, 1, 1).toString()))
                .andDo(print())
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.details[0].fieldName", equalTo("birthdateFrom")))
                .andExpect(jsonPath("$.details[0].errorMessage", equalTo("Date must be before birthdateTo value")))
                .andExpect(jsonPath("$.details[0].passedValue", equalTo("2020-01-01")));
    }

    @Test
    void shouldReturnNotEmptyList_whenTwoUserInRange_andParamsAreValid() throws Exception {
        ReadUserDto readUserDto1 = new ReadUserDto(1L, "stub1", "stub1", "stub1", LocalDate.parse("2010-02-02"), null, null);
        ReadUserDto readUserDto2 = new ReadUserDto(2L, "stub2", "stub2", "stub2", LocalDate.parse("2015-12-02"), "stub2", null);
        List<ReadUserDto> readUserDtoList = List.of(readUserDto1, readUserDto2);
        when(userService.findInBirthdateRange(ArgumentMatchers.any(UserBirthdateRangeQuery.class))).thenReturn(readUserDtoList);

        mockMvc.perform(get("/api/v1/users")
                        .param("birthdateFrom", LocalDate.of(2000, 1, 1).toString())
                        .param("birthdateTo", LocalDate.of(2020, 1, 1).toString()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(readUserDtoList)));
    }

    @ParameterizedTest
    @MethodSource("provideRequestBodyAndExpectedValidationResponseForUpdatingExistedUser")
    void shouldReturnValidationErrors_whenPassedInvalidValuesForUpdatingNotExistedUser(String requestBody, String expectedValidationResponse) throws Exception {
        MvcResult mvcResult = mockMvc.perform(put("/api/v1/users/1")
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn();

        String actualResponse = mvcResult.getResponse().getContentAsString();
        assertEqualsIgnoringTimestamp(expectedValidationResponse, actualResponse);
    }

    public static Stream<Arguments> provideRequestBodyAndExpectedValidationResponseForUpdatingExistedUser() throws IOException {
        return Stream.of(
                Arguments.of(EMPTY_BODY, readFile(EXPECTED_PUT_VALIDATION_RESPONSE_WHEN_NOTHING_PASSED)),
                Arguments.of(readFile(REQUEST_WITH_INVALID_FIRST_NAME_TYPE), readFile(EXPECTED_PUT_VALIDATION_RESPONSE_WHEN_INVALID_FIRST_NAME_TYPE)),
                Arguments.of(readFile(REQUEST_WITH_INVALID_BIRTHDATE_TYPE), readFile(EXPECTED_PUT_VALIDATION_RESPONSE_WHEN_INVALID_BIRTHDATE_TYPE)),
                Arguments.of(readFile(REQUEST_WITH_FUTURE_BIRTHDATE), readFile(EXPECTED_PUT_VALIDATION_RESPONSE_WHEN_BIRTHDAY_IN_FUTURE)),
                Arguments.of(readFile(REQUEST_WITH_UNEXPECTED_CODE), readFile(EXPECTED_PUT_VALIDATION_RESPONSE_WHEN_REQUEST_NOT_PARSABLE))
        );
    }

    @Test
    void shouldReturnValidationErrors_wheUserWithEmailExistsAndUserHasNotItForUpdating() throws Exception {
        String requestBody = readFile(REQUEST_WITH_VALID_VALUES);
        String expectedValidationResponse = readFile(EXPECTED_PUT_VALIDATION_RESPONSE_WHEN_USER_WITH_EMAIL_AND_USER_NOT_HAS_IT);
        when(userService.existByEmail("testuser@gmail.com")).thenReturn(true);
        when(userService.userEmailMatch(1L, "testuser@gmail.com")).thenReturn(false);

        MvcResult mvcResult = mockMvc.perform(put("/api/v1/users/1")
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andReturn();

        String actualResponse = mvcResult.getResponse().getContentAsString();
        assertEqualsIgnoringTimestamp(expectedValidationResponse, actualResponse);
    }

    @Test
    void shouldReturnUpdatedUser_wheUserWithEmailExistsAndUserHasItForUpdating() throws Exception {
        ReadUserDto expectedUpdatedUser = new ReadUserDto(1L, "testuser@gmail.com", "firstName", "lastName", LocalDate.parse("1972-04-29"), "Test address", "+380123456789");
        String requestBody = readFile(REQUEST_WITH_VALID_VALUES);
        String expectedValidationResponse = readFile(EXPECTED_PUT_RESPONSE_WHEN_USER_WITH_EMAIL_AND_USER_HAS_IT);
        when(userService.existByEmail("testuser@gmail.com")).thenReturn(true);
        when(userService.userEmailMatch(1L, "testuser@gmail.com")).thenReturn(true);
        when(userService.update(eq(1L), ArgumentMatchers.any(UpdateUserDto.class))).thenReturn(expectedUpdatedUser);

        MvcResult mvcResult = mockMvc.perform(put("/api/v1/users/1")
                        .content(requestBody)
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andReturn();

        String actualResponse = mvcResult.getResponse().getContentAsString();
        assertEqualsJson(expectedValidationResponse, actualResponse);
    }

    private void assertEqualsIgnoringTimestamp(String expectedResponse, String actualResponse) throws Exception {
        JsonNode expectedNode = objectMapper.readTree(expectedResponse);
        JsonNode actualNode = objectMapper.readTree(actualResponse);

        ((ObjectNode) expectedNode).remove("timestamp");
        ((ObjectNode) actualNode).remove("timestamp");

        compareJson(expectedNode, actualNode);
    }

    private void assertEqualsJson(String expectedResponse, String actualResponse) throws JsonProcessingException, JSONException {
        JsonNode expectedNode = objectMapper.readTree(expectedResponse);
        JsonNode actualNode = objectMapper.readTree(actualResponse);

        compareJson(expectedNode, actualNode);
    }

    private void compareJson(JsonNode expectedNode, JsonNode actualNode) throws JSONException {
        JSONCompareResult result = JSONCompare.compareJSON(expectedNode.toString(), actualNode.toString(), JSONCompareMode.NON_EXTENSIBLE);
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