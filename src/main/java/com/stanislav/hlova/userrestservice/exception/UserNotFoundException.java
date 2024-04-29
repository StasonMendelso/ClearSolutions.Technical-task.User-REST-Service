package com.stanislav.hlova.userrestservice.exception;


import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class UserNotFoundException extends RuntimeException{
    private Long userId;
}

