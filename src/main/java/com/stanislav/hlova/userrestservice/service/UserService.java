package com.stanislav.hlova.userrestservice.service;

import com.stanislav.hlova.userrestservice.dto.ReadUserDto;
import com.stanislav.hlova.userrestservice.dto.RegisterUserDto;
import com.stanislav.hlova.userrestservice.dto.UpdateUserDto;
import com.stanislav.hlova.userrestservice.dto.UserBirthdateRangeQuery;
import com.stanislav.hlova.userrestservice.model.User;

import java.util.List;

public interface UserService {
    User register(RegisterUserDto registerUserDto);
    boolean existByEmail(String email);
    void deleteById(Long userId);

    List<ReadUserDto> findInBirthdateRange(UserBirthdateRangeQuery userBirthdateRangeQuery);

    ReadUserDto update(Long userId, UpdateUserDto updateUserDto);

    boolean userEmailMatch(Long userId, String email);
}
