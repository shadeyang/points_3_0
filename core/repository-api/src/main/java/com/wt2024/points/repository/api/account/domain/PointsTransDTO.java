package com.wt2024.points.repository.api.account.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.wt2024.points.common.Constants;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author Shade.Yang [shade.yang@aliyun.com]
 * @date 2021/12/17 17:00
 * @project points3.0:com.wt2024.points.repository.api.account.domain
 */
@Getter
@Setter
public class PointsTransDTO {

    private Long id;

    private String transNo;

    private String customerId;

    private String pointsTypeNo;

    private String institutionId;

    private String transDate;

    private String transTime;

    private Long transTimestamp;

    private String transTypeNo;

    private String debitOrCredit;

    @JsonFormat(pattern = Constants.DATE_TIME_FORMATTER_YYYYMMDD_HHMMSS, timezone = Constants.TIMEZONE)
    private Date endDate;

    private BigDecimal pointsAmount;

    private String reversedFlag;

    private String oldTransNo;

    private String transChannel;

    private String merchantNo;

    private String voucherTypeNo;

    private String voucherNo;

    private String transStatus;

    private String operator;

    private String sysTransNo;

    private Integer rulesId;

    private String costLine;

    private BigDecimal clearingAmt;

    private String description;
}
