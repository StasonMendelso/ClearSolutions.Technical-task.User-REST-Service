package com.stanislav.hlova.userrestservice.config;

import com.stanislav.hlova.userrestservice.validator.impl.RegisterUserDtoValidator;
import com.stanislav.hlova.userrestservice.validator.impl.UserBirthdateRangeQueryValidator;
import com.stanislav.hlova.userrestservice.validator.impl.UserGenericValidator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

@Configuration
@ComponentScan(basePackages = "com.stanislav.hlova.userrestservice.validator")
public class ValidatorConfig {
    @Bean
    public UserGenericValidator userGenericValidator(RegisterUserDtoValidator registerUserDtoValidator, UserBirthdateRangeQueryValidator userBirthdateRangeQueryValidator) {
        return new UserGenericValidator(registerUserDtoValidator, userBirthdateRangeQueryValidator);
    }
}
