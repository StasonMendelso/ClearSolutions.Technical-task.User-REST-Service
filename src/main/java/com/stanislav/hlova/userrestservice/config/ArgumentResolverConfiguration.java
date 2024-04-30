package com.stanislav.hlova.userrestservice.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.stanislav.hlova.userrestservice.resolver.UpdateUserDtoArgumentResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class ArgumentResolverConfiguration implements WebMvcConfigurer {
    private final ObjectMapper objectMapper;
    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        resolvers.add(new UpdateUserDtoArgumentResolver(objectMapper));
    }
}
