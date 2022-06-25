package com.wt2024.points.core.api.domain.account;

import com.wt2024.points.core.api.jackson.BigDecimalFormat;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;

@Getter
@Setter
public class PointsAccountInfo implements Serializable {

    private String pointsTypeNo;

    @BigDecimalFormat
    private BigDecimal pointsBalance;

    @BigDecimalFormat
    private BigDecimal freezingPoints;

    @BigDecimalFormat
    private BigDecimal inTransitPoints;

    private String pointsAccountStatus;

}
