package com.stanislav.hlova.userrestservice.service.impl;

import com.stanislav.hlova.userrestservice.dto.ReadUserDto;
import com.stanislav.hlova.userrestservice.dto.UpdateUserDto;
import com.stanislav.hlova.userrestservice.exception.UserNotFoundException;
import com.stanislav.hlova.userrestservice.mapper.UserMapper;
import com.stanislav.hlova.userrestservice.model.User;
import com.stanislav.hlova.userrestservice.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {
    @InjectMocks
    private UserServiceImpl userService;
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserMapper userMapper;
    @Mock
    private User user;
    @Mock
    private UpdateUserDto updateUserDto;
    @Mock
    private ReadUserDto readUserDto;


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

    @Test
    void shouldReturnTrue_whenUserHasPassedEmail() {
        Long userId = 1L;
        String email = "test@gmail.com";
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(user.getEmail()).thenReturn(email);

        assertTrue(userService.userEmailMatch(userId, email));
    }

    @Test
    void shouldReturnFalse_whenUserHasNotPassedEmail() {
        Long userId = 1L;
        String email = "test@gmail.com";
        String anotherEmail = "anothertest@gmail.com";
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(user.getEmail()).thenReturn(anotherEmail);

        assertFalse(userService.userEmailMatch(userId, email));
    }

    @Test
    void shouldThrowException_whenUserNotFoundInEmailMatching() {
        Long userId = 1L;
        String email = "test@gmail.com";
        when(userRepository.findById(userId)).thenThrow(new UserNotFoundException(userId));

        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> userService.userEmailMatch(userId, email));
        assertEquals(userId, exception.getUserId());
    }

    @Test
    void shouldThrowException_whenUserNotFoundInUpdating() {
        Long userId = 1L;
        User newUser = mock(User.class);
        when(userMapper.toEntity(updateUserDto)).thenReturn(newUser);
        when(userRepository.findById(userId)).thenThrow(new UserNotFoundException(userId));

        UserNotFoundException exception = assertThrows(UserNotFoundException.class, () -> userService.update(userId, updateUserDto));
        assertEquals(userId, exception.getUserId());
    }


    @Test
    void shouldUpdate_whenUserFoundInUpdating() {
        Long userId = 1L;
        User newUser = mock(User.class);
        when(userMapper.toEntity(updateUserDto)).thenReturn(newUser);
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        when(userMapper.toReadDto(user)).thenReturn(readUserDto);

        assertEquals(readUserDto, userService.update(userId, updateUserDto));
        verify(newUser, times(1)).setId(userId);
        verify(userRepository, times(1)).save(newUser);
    }
}