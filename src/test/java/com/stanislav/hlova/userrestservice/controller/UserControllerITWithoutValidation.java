package com.stanislav.hlova.userrestservice.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.stanislav.hlova.userrestservice.dto.ReadUserDto;
import com.stanislav.hlova.userrestservice.dto.RegisterUserDto;
import com.stanislav.hlova.userrestservice.dto.UpdateUserDto;
import com.stanislav.hlova.userrestservice.dto.UserBirthdateRangeQuery;
import com.stanislav.hlova.userrestservice.exception.UserNotFoundException;
import com.stanislav.hlova.userrestservice.model.User;
import com.stanislav.hlova.userrestservice.service.UserService;
import com.stanislav.hlova.userrestservice.util.UriUtil;
import com.stanislav.hlova.userrestservice.validator.impl.UserGenericValidator;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.net.URI;
import java.time.LocalDate;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(UserController.class)
class UserControllerITWithoutValidation {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private UserService userService;
    @MockBean
    private UserGenericValidator userGenericValidator;
    @MockBean
    private LocalValidatorFactoryBean validator; //disable @Valid in controller methods
    @MockBean
    private UriUtil uriUtil;
    @Mock
    private User user;

    @BeforeEach
    void setUp() {
        when(userGenericValidator.supports(RegisterUserDto.class)).thenReturn(true);
        when(userGenericValidator.supports(UserBirthdateRangeQuery.class)).thenReturn(true);
        when(userGenericValidator.supports(UpdateUserDto.class)).thenReturn(true);
    }

    @Test
    void shouldReturnCreatedStatusAndLocation_whenCreatedUser() throws Exception {
        URI expectedLocation = new URI("/api/v1/users/1");

        when(userService.register(any(RegisterUserDto.class))).thenReturn(user);
        when(user.getId()).thenReturn(1L);
        when(uriUtil.createBaseUri(anyString())).thenReturn(expectedLocation);

        mockMvc.perform(post("/api/v1/users")
                        .content("{}")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(header().string(HttpHeaders.LOCATION, expectedLocation.toString()))
                .andExpect(status().isCreated());
    }

    @Test
    void shouldReturnBadRequest_whenNoContentPassed() throws Exception {
        mockMvc.perform(post("/api/v1/users")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnBadRequest_whenContentPassed_andContentTypeNotPassed() throws Exception {
        mockMvc.perform(post("/api/v1/users")
                        .content("{}"))
                .andExpect(status().isUnsupportedMediaType());
    }

    @Test
    void shouldReturnBadRequest_whenContentPassed_andContentTypeWrong() throws Exception {
        mockMvc.perform(post("/api/v1/users")
                        .content("{}")
                        .contentType(MediaType.APPLICATION_PDF))
                .andExpect(status().isUnsupportedMediaType());
    }

    @Test
    void shouldReturnBadRequest_whenInvalidUserIdPassedForDeleting() throws Exception {
        mockMvc.perform(delete("/api/v1/users/abd"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnBadRequest_whenUserWithPassedIdNotExistForDeleting() throws Exception {
        Long userId = 1L;
        doThrow(new UserNotFoundException(userId)).when(userService).deleteById(userId);

        mockMvc.perform(delete("/api/v1/users/1"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("timestamp").exists())
                .andExpect(jsonPath("status").value(404))
                .andExpect(jsonPath("error").value("Not Found"))
                .andExpect(jsonPath("message").value("User with id 1 wasn't found"));
    }

    @Test
    void shouldReturnOkAndDeleteUser_whenUserWithPassedIdExistForDeleting() throws Exception {
        mockMvc.perform(delete("/api/v1/users/1"))
                .andExpect(status().isOk());

        verify(userService, times(1)).deleteById(1L);
    }

    @Test
    void shouldReturnEmptyList_whenNoUserInRange() throws Exception {
        mockMvc.perform(get("/api/v1/users")
                        .param("birthdateFrom", LocalDate.of(2000, 1, 1).toString())
                        .param("birthdateTo", LocalDate.of(2020, 1, 1).toString()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json("[]"));
    }

    @Test
    void shouldReturnNotEmptyList_whenTwoUserInRange() throws Exception {
        ReadUserDto readUserDto1 = new ReadUserDto(1L, "stub1", "stub1", "stub1", LocalDate.parse("2010-02-02"), null, null);
        ReadUserDto readUserDto2 = new ReadUserDto(2L, "stub2", "stub2", "stub2", LocalDate.parse("2015-12-02"), "stub2", null);
        List<ReadUserDto> readUserDtoList = List.of(readUserDto1, readUserDto2);
        when(userService.findInBirthdateRange(any(UserBirthdateRangeQuery.class))).thenReturn(readUserDtoList);

        mockMvc.perform(get("/api/v1/users")
                        .param("birthdateFrom", LocalDate.of(2000, 1, 1).toString())
                        .param("birthdateTo", LocalDate.of(2020, 1, 1).toString()))
                .andExpect(status().isOk())
                .andExpect(content().contentType(MediaType.APPLICATION_JSON))
                .andExpect(content().json(objectMapper.writeValueAsString(readUserDtoList)));
    }

    @Test
    void shouldReturnBadRequest_whenInvalidUserIdPassedForUpdating() throws Exception {
        mockMvc.perform(put("/api/v1/users/abd")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnBadRequest_whenInvalidContentTypePassedForUpdating() throws Exception {
        mockMvc.perform(put("/api/v1/users/1")
                        .content("{}")
                        .contentType(MediaType.APPLICATION_ATOM_XML))
                .andExpect(status().isUnsupportedMediaType())
                .andExpect(jsonPath("status").value(415))
                .andExpect(jsonPath("title").value("Unsupported Media Type"))
                .andExpect(jsonPath("detail").value("Content-Type 'application/atom+xml' is not supported."))
                .andExpect(jsonPath("instance").value("/api/v1/users/1"));
    }


    @Test
    void shouldReturnBadRequest_whenNoUserFoundForUpdating() throws Exception {
        when(userService.update(eq(1L), any(UpdateUserDto.class))).thenThrow(new UserNotFoundException(1L));

        mockMvc.perform(put("/api/v1/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{}"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("timestamp").exists())
                .andExpect(jsonPath("status").value(404))
                .andExpect(jsonPath("error").value("Not Found"))
                .andExpect(jsonPath("message").value("User with id 1 wasn't found"));
    }

    @Test
    void shouldReturnBadRequest_whenNoContentPassedForUpdating() throws Exception {
        mockMvc.perform(put("/api/v1/users/1")
                        .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("status").value(400))
                .andExpect(jsonPath("title").value("Bad Request"))
                .andExpect(jsonPath("detail").value("Failed to read request"))
                .andExpect(jsonPath("instance").value("/api/v1/users/1"));
    }

    @Test
    void shouldReturnOkAndUpdatedUser_whenValidValuesPassedForUpdating() throws Exception {
        UpdateUserDto updateUserDto = new UpdateUserDto(null, "stubEmail", "stubName", "stubName", LocalDate.now(), "stubAddress", "stubPhone");
        ReadUserDto readUserDto = new ReadUserDto(1L, "stubEmail", "stubName", "stubName", updateUserDto.getBirthdate(), "stubAddress", "stubPhone");
        when(userService.update(eq(1L), any(UpdateUserDto.class))).thenReturn(readUserDto);

        mockMvc.perform(put("/api/v1/users/1")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(updateUserDto)))
                .andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(readUserDto)));
    }

}