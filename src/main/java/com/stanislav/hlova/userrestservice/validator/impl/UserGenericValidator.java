package com.stanislav.hlova.userrestservice.validator.impl;

import com.stanislav.hlova.userrestservice.validator.UserValidator;
import org.springframework.validation.Errors;

import java.util.List;

public class UserGenericValidator implements UserValidator {

    private final List<UserValidator> validatorList;

    public UserGenericValidator(UserValidator... validators) {
        this.validatorList = List.of(validators);
    }

    @Override
    public boolean supports(Class<?> clazz) {
        return validatorList.stream()
                .anyMatch(userValidator -> userValidator.supports(clazz));
    }

    @Override
    public void validate(Object target, Errors errors) {
        validatorList.stream()
                .filter(userValidator -> userValidator.supports(target.getClass()))
                .findFirst()
                .ifPresentOrElse(userValidator -> userValidator.validate(target, errors),
                        () -> {
                            throw new IllegalArgumentException(String.format("No validator was found for validation passed target %s", target));
                        });

    }
}
