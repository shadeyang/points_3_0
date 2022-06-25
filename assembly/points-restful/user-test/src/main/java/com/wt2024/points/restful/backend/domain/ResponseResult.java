package com.wt2024.points.restful.backend.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wt2024.points.common.code.PointsCode;
import lombok.*;

import java.io.Serializable;

/**
 * @Author Shade.Yang [shade.yang@aliyun.com]
 * @Date 2018/9/5 16:39
 * @Project rightsplat:com.uxunchina.rightsplat.dto
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder(alphabetic = true)
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ResponseResult<T> implements Serializable {

    private String code;
    private String message;
    private T data;

    public <T> ResponseResult success() {
        this.setRetCode(PointsCode.TRANS_0000);
        return this;
    }

    public <T> ResponseResult fail(PointsCode pointsCode) {
        this.setRetCode(pointsCode);
        return this;
    }

    public <T> ResponseResult fail(PointsCode pointsCode, String... string) {
        this.setRetCode(pointsCode, string);
        return this;
    }

    @SneakyThrows
    @Override
    public String toString() {
        return new ObjectMapper().writeValueAsString(this);
    }

    public void setRetCode(PointsCode pointsCode) {
        this.code = pointsCode.getCode();
        this.message = pointsCode.getShow();
    }

    public void setRetCode(PointsCode pointsCode, String... string) {
        this.code = pointsCode.getCode();
        this.message = String.format(pointsCode.getShow(), string);
    }

    public boolean convert2Fail(ResponseResult target) {
        if (PointsCode.TRANS_0000.getCode().equals(this.code)) {
            return false;
        } else {
            target.setCode(this.code);
            target.setMessage(this.message);
            return true;
        }
    }
}
