package com.wt2024.points.repository.api.account.domain;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
public class PointsAccountInfoDTO {

    private String customerId;

    private String pointsTypeNo;

    private BigDecimal pointsBalance;

    private BigDecimal freezingPoints;

    private BigDecimal inTransitPoints;

    private String pointsAccountStatus;

    public PointsAccountInfoDTO() {
        this.pointsBalance = BigDecimal.ZERO;
        this.freezingPoints = BigDecimal.ZERO;
        this.inTransitPoints = BigDecimal.ZERO;
    }
}