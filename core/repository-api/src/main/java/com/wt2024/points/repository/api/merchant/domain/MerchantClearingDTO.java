package com.wt2024.points.repository.api.merchant.domain;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * @author Shade.Yang [shade.yang@aliyun.com]
 * @date 2022/6/9 16:30
 * @project points3.0:com.wt2024.points.repository.api.merchant.domain
 */
@Getter
@Setter
public class MerchantClearingDTO {

    private Long id;

    private String merchantNo;

    private String transNo;

    private String transDate;

    private String transTime;

    private String oldTransNo;

    private String sysTransNo;

    private String pointsTypeNo;

    private BigDecimal pointsAmount;

    private String institutionId;

    private String reversedFlag;

    private BigDecimal clearingAmt;

    private String description;

}
