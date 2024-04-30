package com.stanislav.hlova.userrestservice.controller;

import com.stanislav.hlova.userrestservice.dto.ReadUserDto;
import com.stanislav.hlova.userrestservice.dto.RegisterUserDto;
import com.stanislav.hlova.userrestservice.dto.UpdateUserDto;
import com.stanislav.hlova.userrestservice.dto.UserBirthdateRangeQuery;
import com.stanislav.hlova.userrestservice.mapper.UserMapper;
import com.stanislav.hlova.userrestservice.model.User;
import com.stanislav.hlova.userrestservice.service.UserService;
import com.stanislav.hlova.userrestservice.util.PatcherUtil;
import com.stanislav.hlova.userrestservice.util.UriUtil;
import com.stanislav.hlova.userrestservice.validator.impl.UserGenericValidator;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.DataBinder;
import org.springframework.validation.SmartValidator;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;
    private final UriUtil uriUtil;
    private final PatcherUtil patcherUtil;
    private final UserGenericValidator userGenericValidator;
    private final SmartValidator smartValidator;
    private final UserMapper userMapper;

    @InitBinder({"registerUserDto", "userBirthdateRangeQuery", "updateUserDto"})
    public void initBinder(WebDataBinder binder) {
        binder.addValidators(userGenericValidator);
    }

    @PostMapping
    public ResponseEntity<?> register(@RequestBody @Valid RegisterUserDto registerUserDto) {
        User createdUser = userService.register(registerUserDto);

        URI uri = uriUtil.createBaseUri(String.format("/api/v1/users/%d", createdUser.getId()));
        return ResponseEntity.created(uri)
                .build();
    }

    @DeleteMapping("{id}")
    public ResponseEntity<?> delete(@PathVariable(name = "id") Long userId) {
        userService.deleteById(userId);
        return ResponseEntity.ok().build();
    }

    @GetMapping
    public ResponseEntity<List<ReadUserDto>> getUserInBirthdateRange(@Valid UserBirthdateRangeQuery userBirthdateRangeQuery) {
        List<ReadUserDto> userDtoList = userService.findInBirthdateRange(userBirthdateRangeQuery);
        return ResponseEntity.ok(userDtoList);
    }

    @PutMapping(value = "{id}", consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<ReadUserDto> update(@PathVariable(name = "id") Long userId, @Valid UpdateUserDto updateUserDto) {
        ReadUserDto updatedUser = userService.update(userId, updateUserDto);

        return ResponseEntity.ok(updatedUser);
    }

    @PatchMapping(value = "{id}", consumes = {MediaType.APPLICATION_JSON_VALUE})
    public ResponseEntity<ReadUserDto> patch(@PathVariable(name = "id") Long userId, @RequestBody Map<String, Object> data) throws MethodArgumentNotValidException {
        UpdateUserDto updateUserDto = userMapper.toUpdateDto(userService.readById(userId));
        patcherUtil.patch(data, updateUserDto);

        validate(updateUserDto);

        ReadUserDto updatedUser = userService.update(userId, updateUserDto);
        return ResponseEntity.ok(updatedUser);
    }

    protected <T> void validate(T targetObject) throws MethodArgumentNotValidException {
        DataBinder dataBinder = new DataBinder(targetObject);
        dataBinder.addValidators(userGenericValidator, smartValidator);
        dataBinder.validate();
        BindingResult bindingResult = dataBinder.getBindingResult();
        if (bindingResult.hasErrors()) {
            throw new MethodArgumentNotValidException(null, bindingResult);
        }
    }
}

