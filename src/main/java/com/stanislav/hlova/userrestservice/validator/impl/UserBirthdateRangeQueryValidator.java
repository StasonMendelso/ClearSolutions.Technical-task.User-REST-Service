package com.stanislav.hlova.userrestservice.validator.impl;

import com.stanislav.hlova.userrestservice.dto.UserBirthdateRangeQuery;
import com.stanislav.hlova.userrestservice.validator.UserValidator;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

import java.time.LocalDate;


@Component
public class UserBirthdateRangeQueryValidator implements UserValidator {

    private static final String BIRTHDATE_FROM_FIELD_NAME = "birthdateFrom";
    private static final String DATE_MUST_BE_BEFORE_BIRTHDATE_TO_VALUE = "Date must be before birthdateTo value";

    @Override
    public boolean supports(Class<?> clazz) {
        return UserBirthdateRangeQuery.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        UserBirthdateRangeQuery query = (UserBirthdateRangeQuery) target;
        LocalDate birthdateFrom = query.getBirthdateFrom();
        LocalDate birthdateTo = query.getBirthdateTo();
        if (birthdateTo != null && birthdateFrom != null
                && ((birthdateFrom.isAfter(birthdateTo)) || birthdateTo.isEqual(birthdateFrom))) {
            errors.rejectValue(BIRTHDATE_FROM_FIELD_NAME, "", DATE_MUST_BE_BEFORE_BIRTHDATE_TO_VALUE);

        }
    }
}
