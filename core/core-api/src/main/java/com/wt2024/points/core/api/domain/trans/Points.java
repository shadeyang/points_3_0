package com.wt2024.points.core.api.domain.trans;

import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author Shade.Yang [shade.yang@aliyun.com]
 * @date 2022/1/26 09:16
 * @project points3.0:com.wt2024.points.core.api.domain.trans
 */
@Getter
@Setter
public class Points implements Serializable {

    private String customerId;
    @NotEmpty(message = "积分类型不能为空")
    private String pointsTypeNo;
    @NotNull(message = "积分交易金额不能为空")
    @DecimalMin(value = "0.01", message = "积分交易金额必须大于0.00")
    @Digits(integer = 18, fraction = 2, message = "积分交易金额不能超过两位小数")
    private BigDecimal pointsAmount;

    private String endDate;
}
