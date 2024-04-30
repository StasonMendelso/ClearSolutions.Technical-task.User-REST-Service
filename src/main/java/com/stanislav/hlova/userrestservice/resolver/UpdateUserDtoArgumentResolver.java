package com.stanislav.hlova.userrestservice.resolver;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.stanislav.hlova.userrestservice.dto.UpdateUserDto;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.apache.commons.io.IOUtils;
import org.springframework.core.MethodParameter;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

@RequiredArgsConstructor
public class UpdateUserDtoArgumentResolver implements HandlerMethodArgumentResolver {

    private final ObjectMapper objectMapper;

    @Override
    public boolean supportsParameter(MethodParameter parameter) {
        return UpdateUserDto.class.equals(parameter.getParameter().getType());
    }

    @Override
    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        HttpServletRequest request = ((ServletWebRequest) webRequest).getRequest();
        String requestBody = IOUtils.toString(request.getInputStream());
        UpdateUserDto updateUserDto;
        try {
            updateUserDto = objectMapper.readValue(requestBody, UpdateUserDto.class);
        } catch (Exception exception) {
            throw new HttpMessageNotReadableException("Can't read request body.", exception);
        }
        String path = request.getPathInfo() != null ? request.getPathInfo() : request.getServletPath();
        String userId = path.split("users/")[1];
        updateUserDto.setId(Long.valueOf(userId));

        if (parameter.hasParameterAnnotation(Valid.class)) {
            WebDataBinder binder = binderFactory.createBinder(webRequest, updateUserDto, "updateUserDto");
            binder.validate();
            BindingResult bindingResult = binder.getBindingResult();
            if (bindingResult.hasErrors()) {
                throw new MethodArgumentNotValidException(parameter, bindingResult);
            }
        }
        return updateUserDto;
    }

}
