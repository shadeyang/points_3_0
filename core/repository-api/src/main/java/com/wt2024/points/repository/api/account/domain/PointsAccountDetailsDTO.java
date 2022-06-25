package com.wt2024.points.repository.api.account.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.wt2024.points.common.Constants;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.Date;

/**
 * @author Shade.Yang [shade.yang@aliyun.com]
 * @date 2021/12/17 17:19
 * @project points3.0:com.wt2024.points.repository.api.account.domain
 */
@Getter
@Setter
public class PointsAccountDetailsDTO {

    private BigDecimal pointsAmount;

    @JsonFormat(pattern = Constants.DATE_TIME_FORMATTER_YYYYMMDD_HHMMSS, timezone = Constants.TIMEZONE)
    private Date expirationTime;
    
}
