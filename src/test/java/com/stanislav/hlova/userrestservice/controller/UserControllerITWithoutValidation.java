package com.stanislav.hlova.userrestservice.controller;

import com.stanislav.hlova.userrestservice.dto.RegisterUserDto;
import com.stanislav.hlova.userrestservice.exception.UserNotFoundException;
import com.stanislav.hlova.userrestservice.model.User;
import com.stanislav.hlova.userrestservice.service.UserService;
import com.stanislav.hlova.userrestservice.util.UriUtil;
import com.stanislav.hlova.userrestservice.validator.RegisterUserDtoValidator;
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

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@ExtendWith(SpringExtension.class)
@WebMvcTest(UserController.class)
class UserControllerITWithoutValidation {

    @Autowired
    private MockMvc mockMvc;
    @MockBean
    private UserService userService;
    @MockBean
    private RegisterUserDtoValidator registerUserDtoValidator;
    @MockBean
    private LocalValidatorFactoryBean validator; //disable @Valid in controller methods
    @MockBean
    private UriUtil uriUtil;
    @Mock
    private User user;

    @BeforeEach
    void setUp() {
        when(registerUserDtoValidator.supports(RegisterUserDto.class)).thenCallRealMethod();
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
    void shouldReturnBadRequest_whenInvalidUserIdPassed() throws Exception {
        mockMvc.perform(delete("/api/v1/users/abd"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldReturnBadRequest_whenUserWithPassedIdNotExist() throws Exception {
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
    void shouldReturnBadRequest_whenUserWithPassedIdExist() throws Exception {
        mockMvc.perform(delete("/api/v1/users/1"))
                .andExpect(status().isOk());
    }
}