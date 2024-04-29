package com.stanislav.hlova.userrestservice.validator;

import com.stanislav.hlova.userrestservice.dto.RegisterUserDto;
import com.stanislav.hlova.userrestservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;

import java.time.LocalDate;

@Component
@RequiredArgsConstructor
public class RegisterUserDtoValidator implements Validator {
    private static final String EMAIL_FIELD_NAME = "email";
    private static final String BIRTHDATE_FIELD_NAME = "birthdate";
    private final UserService userService;
    @Value("${user.register.minimal-age}")
    private int registrationAge;

    @Override
    public boolean supports(Class<?> clazz) {
        return RegisterUserDto.class.equals(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        RegisterUserDto registerUserDto = (RegisterUserDto) target;

        if (userService.existByEmail(registerUserDto.getEmail())) {
            errors.rejectValue(EMAIL_FIELD_NAME, "", "User with passed email already exists");
        }
        if (registerUserDto.getBirthdate() == null || LocalDate.now().minusYears(registrationAge).isBefore(registerUserDto.getBirthdate())
                && registerUserDto.getBirthdate().isBefore(LocalDate.now())) {
            errors.rejectValue(BIRTHDATE_FIELD_NAME, "", String.format("You age must be more than %d", registrationAge));
        }
    }
}
