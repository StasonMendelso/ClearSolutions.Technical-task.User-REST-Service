package com.stanislav.hlova.userrestservice.validator.impl;

import com.stanislav.hlova.userrestservice.dto.UpdateUserDto;
import com.stanislav.hlova.userrestservice.service.UserService;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;

@Component
public class UpdateUserValidator  extends AbstractUserValidator  {
    private static final String EMAIL_FIELD_NAME = "email";
    private static final String BIRTHDATE_FIELD_NAME = "birthdate";

    public UpdateUserValidator(UserService userService) {
        super(userService);
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return UpdateUserDto.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        UpdateUserDto updateUserDto = (UpdateUserDto) target;

        validateEmail(errors, updateUserDto.getId(), updateUserDto.getEmail(), EMAIL_FIELD_NAME);
        validateBirthdate(errors, updateUserDto.getBirthdate(), BIRTHDATE_FIELD_NAME);
    }

    protected void validateEmail(Errors errors, Long userId, String email, String emailFieldName) {
        if (userService.existByEmail(email) && !userService.userEmailMatch(userId,email)) {
            errors.rejectValue(emailFieldName, "", "User with passed email already exists");
        }
    }
}
