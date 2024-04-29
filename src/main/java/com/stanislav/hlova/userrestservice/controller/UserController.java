package com.stanislav.hlova.userrestservice.controller;

import com.stanislav.hlova.userrestservice.dto.RegisterUserDto;
import com.stanislav.hlova.userrestservice.model.User;
import com.stanislav.hlova.userrestservice.service.UserService;
import com.stanislav.hlova.userrestservice.util.UriUtil;
import com.stanislav.hlova.userrestservice.validator.RegisterUserDtoValidator;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import java.net.URI;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final UriUtil uriUtil;
    private final RegisterUserDtoValidator registerUserDtoValidator;

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        binder.addValidators(registerUserDtoValidator);
    }

    @PostMapping
    public ResponseEntity<?> register(@RequestBody @Valid RegisterUserDto registerUserDto) {
        User createdUser = userService.register(registerUserDto);

        URI uri = uriUtil.createBaseUri(String.format("/api/v1/users/%d", createdUser.getId()));
        return ResponseEntity.created(uri)
                .build();
    }
}
