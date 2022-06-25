package com.wt2024.points.repository.api.account.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.wt2024.points.common.Constants;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author Shade.Yang [shade.yang@aliyun.com]
 * @date 2022/1/27 08:59
 * @project points3.0:com.wt2024.points.repository.api.account.domain
 */
@Getter
@Setter
public class PointsTransDetailsDTO {

    private String transNo;

    private String customerId;

    private String sourceTransNo;

    private String pointsTypeNo;

    @JsonFormat(pattern = Constants.DATE_TIME_FORMATTER_YYYYMMDD_HHMMSS, timezone = Constants.TIMEZONE)
    private Date endDate;

    private BigDecimal pointsAmount;

    private String merchantNo;

    private String costLine;

    private BigDecimal clearingAmt;

}
