package com.wt2024.points.core.api.domain.trans;

import com.wt2024.points.core.api.domain.InputBase;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.Digits;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.math.BigDecimal;

/**
 * @author Shade.Yang [shade.yang@aliyun.com]
 * @date 2022/5/18 16:39
 * @project points3.0:com.wt2024.points.core.api.domain.trans
 */
@Setter
@Getter
public class PointsBackInput extends InputBase {

    @NotEmpty(message = "发起流水号不能为空")
    private String sysTransNo;

    @NotEmpty(message = "需要退货的流水号不能为空")
    private String backSysTransNo;

    private String operator;

    @NotNull(message = "退货积分不能为空")
    @DecimalMin(value = "0", message = "退货积分必须大于0")
    @Digits(integer = 18, fraction = 2, message = "积分交易金额不能超过两位小数")
    private BigDecimal pointsAmount;
}
