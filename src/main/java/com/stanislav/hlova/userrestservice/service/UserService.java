package com.stanislav.hlova.userrestservice.service;

import com.stanislav.hlova.userrestservice.dto.RegisterUserDto;
import com.stanislav.hlova.userrestservice.model.User;

public interface UserService {
    User register(RegisterUserDto registerUserDto);

    boolean existByEmail(String email);
}
