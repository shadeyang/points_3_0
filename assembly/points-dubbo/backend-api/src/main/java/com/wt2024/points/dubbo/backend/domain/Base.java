package com.wt2024.points.dubbo.backend.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;

import javax.validation.constraints.NotEmpty;
import java.io.Serializable;

/**
 * @Author Shade.Yang [shade.yang@aliyun.com]
 * @Date 2018/9/5 16:39
 * @Project rightsplat:com.wt2024.points.dto
 */
@ApiModel(value = "基础通讯类")
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder(alphabetic = true)
@Getter
@Setter
public abstract class Base implements Serializable {

    @NotEmpty
    @ApiModelProperty(value = "机构编号", required = true)
    private String institutionNo;

    @SneakyThrows
    @Override
    public String toString() {
        return new ObjectMapper().writeValueAsString(this);
    }

}
