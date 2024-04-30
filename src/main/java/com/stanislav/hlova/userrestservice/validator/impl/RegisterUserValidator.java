package com.stanislav.hlova.userrestservice.validator.impl;

import com.stanislav.hlova.userrestservice.dto.RegisterUserDto;
import com.stanislav.hlova.userrestservice.service.UserService;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

@Component
public class RegisterUserValidator extends AbstractUserValidator {
    private static final String EMAIL_FIELD_NAME = "email";
    private static final String BIRTHDATE_FIELD_NAME = "birthdate";

    public RegisterUserValidator(UserService userService) {
        super(userService);
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return RegisterUserDto.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        RegisterUserDto registerUserDto = (RegisterUserDto) target;

        validateEmail(errors, registerUserDto.getEmail(), EMAIL_FIELD_NAME);
        validateBirthdate(errors, registerUserDto.getBirthdate(), BIRTHDATE_FIELD_NAME);
    }
}
