package com.wt2024.points.repository.api.account.domain;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author Shade.Yang [shade.yang@aliyun.com]
 * @date 2022/2/16 15:05
 * @project points3.0:com.wt2024.points.repository.api.account.domain
 */
@Getter
@Setter
public class PointsDetailsBalanceDTO {

    private String transNo;

    private String customerId;

    private String pointsTypeNo;

    private Date endDate;

    private BigDecimal pointsAmount;

}
