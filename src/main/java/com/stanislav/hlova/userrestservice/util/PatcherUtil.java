package com.stanislav.hlova.userrestservice.util;

import lombok.RequiredArgsConstructor;
import org.springframework.core.convert.ConversionService;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.stereotype.Component;

import java.lang.reflect.Field;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class PatcherUtil {
    private final ConversionService conversionService;

    public <T> T patch(Map<String, Object> data, T destinationObject) {
        Field[] fields = destinationObject.getClass().getDeclaredFields();
        try {
            for (Field field : fields) {
                if (field.getName().equals("id")){
                    continue;
                }
                if (data.containsKey(field.getName())) {
                    Object value = data.get(field.getName());
                    setFieldValue(field, destinationObject, value);
                }
            }
        } catch (Exception exception) {
            throw new HttpMessageNotReadableException(String.format("Can't parse data to destinationObject. Data: %s. Destination Object: %s", data, destinationObject), exception);
        }
        return destinationObject;
    }

    private <T> void setFieldValue(Field field, T destinationObject, Object value) throws IllegalAccessException {
        Object converted = conversionService.convert(value, field.getType());
        field.setAccessible(true);
        field.set(destinationObject, converted);
    }
}
