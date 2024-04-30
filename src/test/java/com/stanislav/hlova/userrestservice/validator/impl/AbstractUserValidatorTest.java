package com.stanislav.hlova.userrestservice.validator.impl;

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
class AbstractUserValidatorTest {

    public static final int REGISTRATION_AGE = 5;
    private static final String EMAIL_FIELD_NAME = "email";
    private static final String BIRTHDATE_FIELD_NAME = "birthdate";
    public static final String USER_WITH_PASSED_EMAIL_ALREADY_EXISTS = "User with passed email already exists";
    public static final String YOU_AGE_MUST_BE_MORE_THAN_5 = "You age must be more than 5";
    @InjectMocks
    private StubTestValidator validator;
    @Mock
    private UserService userService;
    @Mock
    private Errors errors;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(validator, "registrationAge", REGISTRATION_AGE);
    }

    @Test
    void shouldNotPutError_whenPassEmailWhichNotExists() {
        String email = "test@gmail.com";

        validator.validateEmail(errors, email, EMAIL_FIELD_NAME);

        verifyNoInteractions(errors);
    }

    @Test
    void shouldPutError_whenPassEmailWhichExists() {
        String email = "test@gmail.com";
        when(userService.existByEmail(email)).thenReturn(true);

        validator.validateEmail(errors, email, EMAIL_FIELD_NAME);

        verify(errors, times(1)).rejectValue(EMAIL_FIELD_NAME, "", USER_WITH_PASSED_EMAIL_ALREADY_EXISTS);
    }

    @Test
    void shouldNotPutError_whenPassCorrectBirthdateEqualRequiredAge() {
        validator.validateBirthdate(errors, LocalDate.now().minusYears(REGISTRATION_AGE), BIRTHDATE_FIELD_NAME);

        verifyNoInteractions(errors);
    }

    @Test
    void shouldNotPutError_whenPassCorrectBirthdateMoreRequiredAge() {
        validator.validateBirthdate(errors, LocalDate.now().minusYears(REGISTRATION_AGE).minusDays(1), BIRTHDATE_FIELD_NAME);

        verifyNoInteractions(errors);
    }

    @Test
    void shouldPutError_whenPassCorrectBirthdateLessRequiredAge() {
        validator.validateBirthdate(errors, LocalDate.now().minusYears(REGISTRATION_AGE).plusDays(1), BIRTHDATE_FIELD_NAME);

        verify(errors, times(1)).rejectValue(BIRTHDATE_FIELD_NAME, "", YOU_AGE_MUST_BE_MORE_THAN_5);
    }

    @Test
    void shouldPutError_whenPassNull() {
        validator.validateBirthdate(errors, null, BIRTHDATE_FIELD_NAME);

        verify(errors, times(1)).rejectValue(BIRTHDATE_FIELD_NAME, "", YOU_AGE_MUST_BE_MORE_THAN_5);
    }

    public static class StubTestValidator extends AbstractUserValidator {

        public StubTestValidator(UserService userService) {
            super(userService);
        }

        @Override
        public boolean supports(Class<?> clazz) {
            return false;
        }

        @Override
        public void validate(Object target, Errors errors) {

        }
    }

}