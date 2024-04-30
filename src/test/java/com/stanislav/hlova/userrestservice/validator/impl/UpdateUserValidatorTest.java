package com.stanislav.hlova.userrestservice.validator.impl;

import com.stanislav.hlova.userrestservice.dto.UpdateUserDto;
import com.stanislav.hlova.userrestservice.service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.validation.Errors;

import java.time.LocalDate;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UpdateUserValidatorTest {

    public static final int REGISTRATION_AGE = 5;
    private static final String EMAIL_FIELD_NAME = "email";
    private static final String BIRTHDATE_FIELD_NAME = "birthdate";
    public static final String USER_WITH_PASSED_EMAIL_ALREADY_EXISTS = "User with passed email already exists";
    public static final String YOU_AGE_MUST_BE_MORE_THAN_5 = "You age must be more than 5";
    @InjectMocks
    private UpdateUserValidator validator;
    @Mock
    private UserService userService;
    @Mock
    private Errors errors;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(validator, "registrationAge", REGISTRATION_AGE);
    }

    @Test
    void shouldNotPutError_whenPassValidDto() {
        UpdateUserDto updateUserDto = UpdateUserDto.builder()
                .birthdate(LocalDate.now().minusYears(REGISTRATION_AGE).minusDays(1))
                .email("test@gmail.com")
                .build();

        validator.validate(updateUserDto, errors);

        verifyNoInteractions(errors);
    }

    @Test
    void shouldPutError_whenUserWithEmailExistsAndEmailNotForCurrentUser() {
        String existedEmail = "test@gmail.com";
        Long userId = 1L;
        when(userService.existByEmail(existedEmail)).thenReturn(true);
        when(userService.userEmailMatch(userId, existedEmail)).thenReturn(false);
        UpdateUserDto updateUserDto = UpdateUserDto.builder()
                .email(existedEmail)
                .id(userId)
                .birthdate(LocalDate.now().minusYears(6))
                .build();

        validator.validate(updateUserDto, errors);

        verify(errors, times(1)).rejectValue(EMAIL_FIELD_NAME, "", USER_WITH_PASSED_EMAIL_ALREADY_EXISTS);
    }

    @Test
    void shouldPutError_whenAgeLessRequired() {
        UpdateUserDto updateUserDto = UpdateUserDto.builder()
                .birthdate(LocalDate.now().minusYears(1))
                .build();

        validator.validate(updateUserDto, errors);

        verify(errors, times(1)).rejectValue(BIRTHDATE_FIELD_NAME, "", YOU_AGE_MUST_BE_MORE_THAN_5);
    }

    @Test
    void shouldPutError_whenAgeLessByOneDay() {
        UpdateUserDto updateUserDto = UpdateUserDto.builder()
                .birthdate(LocalDate.now().minusYears(REGISTRATION_AGE).plusDays(1))
                .build();

        validator.validate(updateUserDto, errors);

        verify(errors, times(1)).rejectValue(BIRTHDATE_FIELD_NAME, "", YOU_AGE_MUST_BE_MORE_THAN_5);
    }

    @Test
    void shouldNotPutError_whenAgeEqualsRequired() {
        UpdateUserDto updateUserDto = UpdateUserDto.builder()
                .birthdate(LocalDate.now().minusYears(REGISTRATION_AGE))
                .build();

        validator.validate(updateUserDto, errors);

        verify(errors, times(0)).rejectValue(BIRTHDATE_FIELD_NAME, "", YOU_AGE_MUST_BE_MORE_THAN_5);
    }

    @Test
    void shouldNotPutError_whenAgeBiggerByOneDay() {
        UpdateUserDto updateUserDto = UpdateUserDto.builder()
                .birthdate(LocalDate.now().minusYears(REGISTRATION_AGE).minusDays(1))
                .build();

        validator.validate(updateUserDto, errors);

        verify(errors, times(0)).rejectValue(BIRTHDATE_FIELD_NAME, "", YOU_AGE_MUST_BE_MORE_THAN_5);
    }

    @Test
    void shouldPutError_whenBirthdateIsNull() {
        UpdateUserDto updateUserDto = UpdateUserDto.builder().build();

        validator.validate(updateUserDto, errors);

        verify(errors, times(1)).rejectValue(BIRTHDATE_FIELD_NAME, "", YOU_AGE_MUST_BE_MORE_THAN_5);
    }

    @Test
    void shouldPutError_whenAgeLessRequired_andUserWithEmailExists() {
        String existedEmail = "test@gmail.com";
        when(userService.existByEmail(existedEmail)).thenReturn(true);
        UpdateUserDto updateUserDto = UpdateUserDto.builder()
                .birthdate(LocalDate.now().minusYears(1))
                .email(existedEmail)
                .build();

        validator.validate(updateUserDto, errors);

        verify(errors, times(1)).rejectValue(BIRTHDATE_FIELD_NAME, "", YOU_AGE_MUST_BE_MORE_THAN_5);
        verify(errors, times(1)).rejectValue(EMAIL_FIELD_NAME, "", USER_WITH_PASSED_EMAIL_ALREADY_EXISTS);
    }
}