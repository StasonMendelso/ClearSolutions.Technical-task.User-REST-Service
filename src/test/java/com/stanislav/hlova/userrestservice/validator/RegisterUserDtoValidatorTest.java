package com.stanislav.hlova.userrestservice.validator;

import com.stanislav.hlova.userrestservice.dto.RegisterUserDto;
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
class RegisterUserDtoValidatorTest {
    public static final int REGISTRATION_AGE = 5;
    private static final String EMAIL_FIELD_NAME = "email";
    private static final String BIRTHDATE_FIELD_NAME = "birthdate";
    public static final String USER_WITH_PASSED_EMAIL_ALREADY_EXISTS = "User with passed email already exists";
    public static final String YOU_AGE_MUST_BE_MORE_THAN_5 = "You age must be more than 5";
    @InjectMocks
    private RegisterUserDtoValidator validator;
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
        RegisterUserDto registerUserDto = RegisterUserDto.builder()
                .birthdate(LocalDate.now().minusYears(6))
                .build();

        validator.validate(registerUserDto, errors);

        verifyNoInteractions(errors);
    }

    @Test
    void shouldPutError_whenUserWithEmailExists() {
        String existedEmail = "test@gmail.com";
        when(userService.existByEmail(existedEmail)).thenReturn(true);
        RegisterUserDto registerUserDto = RegisterUserDto.builder()
                .email(existedEmail)
                .birthdate(LocalDate.now().minusYears(6))
                .build();

        validator.validate(registerUserDto, errors);

        verify(errors, times(1)).rejectValue(EMAIL_FIELD_NAME, "", USER_WITH_PASSED_EMAIL_ALREADY_EXISTS);
    }

    @Test
    void shouldPutError_whenAgeLessRequired() {
        RegisterUserDto registerUserDto = RegisterUserDto.builder()
                .birthdate(LocalDate.now().minusYears(1))
                .build();

        validator.validate(registerUserDto, errors);

        verify(errors, times(1)).rejectValue(BIRTHDATE_FIELD_NAME, "", YOU_AGE_MUST_BE_MORE_THAN_5);
    }
    @Test
    void shouldPutError_whenAgeLessByOneDay() {
        RegisterUserDto registerUserDto = RegisterUserDto.builder()
                .birthdate(LocalDate.now().minusYears(REGISTRATION_AGE).plusDays(1))
                .build();

        validator.validate(registerUserDto, errors);

        verify(errors, times(1)).rejectValue(BIRTHDATE_FIELD_NAME, "", YOU_AGE_MUST_BE_MORE_THAN_5);
    }
    @Test
    void shouldNotPutError_whenAgeEqualsRequired() {
        RegisterUserDto registerUserDto = RegisterUserDto.builder()
                .birthdate(LocalDate.now().minusYears(REGISTRATION_AGE))
                .build();

        validator.validate(registerUserDto, errors);

        verify(errors, times(0)).rejectValue(BIRTHDATE_FIELD_NAME, "", YOU_AGE_MUST_BE_MORE_THAN_5);
    }
    @Test
    void shouldNotPutError_whenAgeBiggerByOneDay() {
        RegisterUserDto registerUserDto = RegisterUserDto.builder()
                .birthdate(LocalDate.now().minusYears(REGISTRATION_AGE).minusDays(1))
                .build();

        validator.validate(registerUserDto, errors);

        verify(errors, times(0)).rejectValue(BIRTHDATE_FIELD_NAME, "", YOU_AGE_MUST_BE_MORE_THAN_5);
    }

    @Test
    void shouldPutError_whenBirthdateIsNull() {
        RegisterUserDto registerUserDto = RegisterUserDto.builder().build();

        validator.validate(registerUserDto, errors);

        verify(errors, times(1)).rejectValue(BIRTHDATE_FIELD_NAME, "", YOU_AGE_MUST_BE_MORE_THAN_5);
    }

    @Test
    void shouldPutError_whenAgeLessRequired_andUserWithEmailExists() {
        String existedEmail = "test@gmail.com";
        when(userService.existByEmail(existedEmail)).thenReturn(true);
        RegisterUserDto registerUserDto = RegisterUserDto.builder()
                .birthdate(LocalDate.now().minusYears(1))
                .email(existedEmail)
                .build();

        validator.validate(registerUserDto, errors);

        verify(errors, times(1)).rejectValue(BIRTHDATE_FIELD_NAME, "", YOU_AGE_MUST_BE_MORE_THAN_5);
        verify(errors, times(1)).rejectValue(EMAIL_FIELD_NAME, "", USER_WITH_PASSED_EMAIL_ALREADY_EXISTS);
    }
}