package com.stanislav.hlova.userrestservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
public class UpdateUserDto {
    private Long id;

    @NotEmpty(message = "Email must be presented.")
    @Email(message = "Invalid email pattern.")
    private String email;

    @NotEmpty(message = "First name must be presented.")
    private String firstName;

    @NotEmpty(message = "Last name must be presented.")
    private String lastName;

    @NotNull(message = "Birthdate must be presented.")
    @Past(message = "Birthdate must be earlier than current date.")
    private LocalDate birthdate;

    private String address;

    private String phoneNumber;
}
