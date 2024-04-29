package com.stanislav.hlova.userrestservice.validator.impl;


import com.stanislav.hlova.userrestservice.dto.UserBirthdateRangeQuery;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.validation.Errors;

import java.time.LocalDate;

import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserBirthdateRangeQueryValidatorTest {
    private static final String BIRTHDATE_FROM_FIELD_NAME = "birthdateFrom";
    private static final String DATE_MUST_BE_BEFORE_BIRTHDATE_TO_VALUE = "Date must be before birthdateTo value";

    private UserBirthdateRangeQueryValidator validator = new UserBirthdateRangeQueryValidator();
    @Mock
    private Errors errors;

    @ParameterizedTest
    @CsvSource(delimiter = ',',
            value = {"2000-01-01,2030-01-01", "2020-02-02,2060-11-29", "1950-01-01,2024-12-02"}
    )
    void shouldNotPutError_whenBirthDateFromBeforeTo(LocalDate from, LocalDate to) {
        UserBirthdateRangeQuery query = new UserBirthdateRangeQuery(from, to);

        validator.validate(query, errors);

        verifyNoInteractions(errors);
    }

    @ParameterizedTest
    @CsvSource(delimiter = ',',
            value = {"2040-01-01,2030-01-01", "2020-02-02,2020-02-02", "2024-12-02,1950-01-01"}
    )
    void shouldPutError_whenBirthDateFromAfterOrEqualTo(LocalDate from, LocalDate to) {
        UserBirthdateRangeQuery query = new UserBirthdateRangeQuery(from, to);

        validator.validate(query, errors);

        verify(errors, times(1)).rejectValue(BIRTHDATE_FROM_FIELD_NAME, "", DATE_MUST_BE_BEFORE_BIRTHDATE_TO_VALUE);
    }
}