package com.wt2024.points.core.api.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;

import java.io.Serializable;

/**
 * @Author Shade.Yang [shade.yang@aliyun.com]
 * @Date 2018/9/5 16:39
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder(alphabetic = true)
@Getter
@Setter
public abstract class OutputBase implements Serializable {
    private final static ObjectMapper objectMapper = new ObjectMapper();

    @SneakyThrows
    @Override
    public String toString() {
        return objectMapper.writeValueAsString(this);
    }

}
