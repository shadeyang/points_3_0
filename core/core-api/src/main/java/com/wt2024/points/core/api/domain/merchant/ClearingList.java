package com.wt2024.points.core.api.domain.merchant;

import com.wt2024.points.common.enums.ReversedFlag;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.math.BigDecimal;

/**
 * @author Shade.Yang [shade.yang@aliyun.com]
 * @date 2022/6/9 15:38
 * @project points3.0:com.wt2024.points.core.api.domain.merchant
 */
@Getter
@Setter
public class ClearingList implements Serializable {

    private Long id;
    @NotEmpty(message = "清算商户编号不能为空")
    private String merchantNo;
    @NotEmpty(message = "交易流水不能为空")
    private String transNo;
    @NotEmpty(message = "交易日期不能为空")
    private String transDate;
    @NotEmpty(message = "交易时间不能为空")
    private String transTime;

    private String oldTransNo;
    @NotEmpty(message = "前置流水不能为空")
    private String sysTransNo;
    @NotEmpty(message = "积分类型不能为空")
    private String pointsTypeNo;
    @NotNull(message = "积分交易金额不能为空")
    private BigDecimal pointsAmount;
    @NotEmpty(message = "商户机构不能为空")
    private String institutionId;
    @NotNull(message = "交易类型不能为空")
    private ReversedFlag reversedFlag;
    @NotNull(message = "清算金额不能为空")
    private BigDecimal clearingAmt;

    private String description;

}
