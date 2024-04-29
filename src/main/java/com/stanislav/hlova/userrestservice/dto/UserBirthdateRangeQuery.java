package com.stanislav.hlova.userrestservice.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class UserBirthdateRangeQuery {
    @NotNull(message = "Must be presented.")
    private LocalDate birthdateFrom;
    @NotNull(message = "Must be presented.")
    private LocalDate birthdateTo;
}
