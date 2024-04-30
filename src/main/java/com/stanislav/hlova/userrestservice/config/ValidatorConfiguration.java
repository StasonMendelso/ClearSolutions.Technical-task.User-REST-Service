package com.stanislav.hlova.userrestservice.config;

import com.stanislav.hlova.userrestservice.validator.impl.RegisterUserValidator;
import com.stanislav.hlova.userrestservice.validator.impl.UpdateUserValidator;
import com.stanislav.hlova.userrestservice.validator.impl.UserBirthdateRangeQueryValidator;
import com.stanislav.hlova.userrestservice.validator.impl.UserGenericValidator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = "com.stanislav.hlova.userrestservice.validator")
public class ValidatorConfiguration {
    @Bean
    public UserGenericValidator userGenericValidator(RegisterUserValidator registerUserDtoValidator,
                                                     UserBirthdateRangeQueryValidator userBirthdateRangeQueryValidator,
                                                     UpdateUserValidator updateUserValidator) {
        return new UserGenericValidator(registerUserDtoValidator, userBirthdateRangeQueryValidator, updateUserValidator);
    }
}
