package com.stanislav.hlova.userrestservice.validator.impl;

import com.stanislav.hlova.userrestservice.service.UserService;
import com.stanislav.hlova.userrestservice.validator.UserValidator;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.validation.Errors;

import java.time.LocalDate;

@RequiredArgsConstructor
public abstract class AbstractUserValidator implements UserValidator {
    protected final UserService userService;
    @Value("${user.register.minimal-age}")
    private int registrationAge;
    protected void validateEmail(Errors errors, String email, String emailFieldName){
        if (userService.existByEmail(email)) {
            errors.rejectValue(emailFieldName, "", "User with passed email already exists");
        }
    }
    protected void validateBirthdate(Errors errors, LocalDate birthdate, String birthdateFieldName){
        if (birthdate == null || LocalDate.now().minusYears(registrationAge).isBefore(birthdate)
                && birthdate.isBefore(LocalDate.now())) {
            errors.rejectValue(birthdateFieldName, "", String.format("You age must be more than %d", registrationAge));
        }
    }
}
