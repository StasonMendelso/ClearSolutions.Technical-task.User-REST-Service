package com.stanislav.hlova.userrestservice.service.impl;

import com.stanislav.hlova.userrestservice.exception.UserNotFoundException;
import com.stanislav.hlova.userrestservice.model.User;
import com.stanislav.hlova.userrestservice.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    @InjectMocks
    private UserServiceImpl userService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private User user;


    @Test
    void shouldThrowException_whenUserNotExist() {
        Long userId = 1L;

        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> userService.deleteById(userId));

        assertEquals(userId, exception.getUserId());
    }


    @Test
    void shouldDeleteUser_whenUserExist() {
        Long userId = 1L;
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        userService.deleteById(userId);

        verify(userRepository, times(1)).deleteById(userId);
    }
}