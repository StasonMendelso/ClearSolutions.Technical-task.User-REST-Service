package com.stanislav.hlova.userrestservice.mapper;

import com.stanislav.hlova.userrestservice.dto.ReadUserDto;
import com.stanislav.hlova.userrestservice.dto.RegisterUserDto;
import com.stanislav.hlova.userrestservice.dto.UpdateUserDto;
import com.stanislav.hlova.userrestservice.model.User;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {
    User toEntity(RegisterUserDto registerUserDto);
    User toEntity(UpdateUserDto updateUserDto);
    ReadUserDto toReadDto(User user);
}
