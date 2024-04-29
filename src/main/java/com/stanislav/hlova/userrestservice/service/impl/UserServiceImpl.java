package com.stanislav.hlova.userrestservice.service.impl;

import com.stanislav.hlova.userrestservice.dto.ReadUserDto;
import com.stanislav.hlova.userrestservice.dto.RegisterUserDto;
import com.stanislav.hlova.userrestservice.dto.UserBirthdateRangeQuery;
import com.stanislav.hlova.userrestservice.exception.UserNotFoundException;
import com.stanislav.hlova.userrestservice.mapper.UserMapper;
import com.stanislav.hlova.userrestservice.model.User;
import com.stanislav.hlova.userrestservice.repository.UserRepository;
import com.stanislav.hlova.userrestservice.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final UserMapper userMapper;

    @Override
    public User register(RegisterUserDto registerUserDto) {
        return userRepository.save(userMapper.toEntity(registerUserDto));
    }

    @Override
    public boolean existByEmail(String email) {
        return userRepository.findByEmail(email)
                .isPresent();
    }

    @Override
    public void deleteById(Long userId) {
        userRepository.findById(userId)
                .ifPresentOrElse(user -> userRepository.deleteById(userId), () -> {
                    throw new UserNotFoundException(userId);
                });
    }

    @Override
    public List<ReadUserDto> findInBirthdateRange(UserBirthdateRangeQuery userBirthdateRangeQuery) {
        return userRepository.findUsersByBirthdateGreaterThanEqualAndBirthdateLessThanEqual(userBirthdateRangeQuery.getBirthdateFrom(), userBirthdateRangeQuery.getBirthdateTo())
                .stream()
                .map(userMapper::toReadDto)
                .collect(Collectors.toList());
    }
}
