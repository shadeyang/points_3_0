package com.wt2024.base.entity;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;

import java.io.Serializable;

/**
 * @Author Shade.Yang [shade.yang@aliyun.com]
 * @Date 2018/8/29 10:37
 * @Project rightsplat:com.wt2024.base.entity
 */
public class BaseEntity implements Serializable {

    @SneakyThrows
    @Override
    public String toString() {
        return new ObjectMapper().writeValueAsString(this);
    }

}
