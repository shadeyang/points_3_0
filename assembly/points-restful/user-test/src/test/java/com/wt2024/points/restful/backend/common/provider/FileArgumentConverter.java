package com.wt2024.points.restful.backend.common.provider;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.params.converter.ArgumentConversionException;
import org.junit.jupiter.params.converter.SimpleArgumentConverter;

import java.text.SimpleDateFormat;

public class FileArgumentConverter extends SimpleArgumentConverter {

    private static ObjectMapper objectMapper = new ObjectMapper();

    static {
        objectMapper.setConfig(objectMapper.getDeserializationConfig().with(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")));
        objectMapper.setConfig(objectMapper.getSerializationConfig().with(new SimpleDateFormat("yyyy-MM-dd HH:mm:ss")));
    }

    @Override
    protected Object convert(Object source, Class<?> targetType) throws ArgumentConversionException {
        try {
            if (targetType.isPrimitive() || targetType.isAssignableFrom(String.class)) {
                return targetType.cast(source);
            }
            if (targetType.isAssignableFrom(JsonNode.class)) {
                return objectMapper.readTree(source.toString());
            }
            return objectMapper.readValue(source.toString(), targetType);
        } catch (Exception ex) {
            throw new ArgumentConversionException(String.format("Can not convert %s to %s.", source, targetType), ex);
        }
    }
}
