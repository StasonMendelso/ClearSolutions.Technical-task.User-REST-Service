package com.stanislav.hlova.userrestservice.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
public class ReadUserDto {
    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private LocalDate birthdate;
    private String address;
    private String phoneNumber;
}
