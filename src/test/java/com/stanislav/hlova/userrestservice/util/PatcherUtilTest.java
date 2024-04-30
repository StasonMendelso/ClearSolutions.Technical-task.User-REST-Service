package com.stanislav.hlova.userrestservice.util;

import com.stanislav.hlova.userrestservice.dto.UpdateUserDto;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.core.convert.ConversionService;

import java.time.LocalDate;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PatcherUtilTest {
    @InjectMocks
    private PatcherUtil patcherUtil;
    @Mock
    private ConversionService conversionService;

    @Test
    void shouldPatchValueInDto() {
        UpdateUserDto actual = UpdateUserDto.builder().build();
        UpdateUserDto expected = UpdateUserDto.builder()
                .id(null)
                .email("testEmail")
                .address("address")
                .birthdate(LocalDate.now())
                .firstName("firstName")
                .lastName("lastName")
                .phoneNumber("+3801223")
                .build();
        Map<String, Object> data = Map.of("id", 1,
                "email", expected.getEmail(),
                "address", expected.getAddress(),
                "birthdate", expected.getBirthdate(),
                "firstName", expected.getFirstName(),
                "lastName", expected.getLastName(),
                "phoneNumber", expected.getPhoneNumber());
        when(conversionService.convert(any(Object.class), any(Class.class))).thenAnswer(invocation -> invocation.getArgument(0));

        actual = patcherUtil.patch(data, actual);

        assertEquals(expected, actual);
    }
}