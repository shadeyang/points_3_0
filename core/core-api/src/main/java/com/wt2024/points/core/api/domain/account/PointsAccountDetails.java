package com.wt2024.points.core.api.domain.account;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.wt2024.points.common.Constants;
import com.wt2024.points.core.api.domain.trans.PointsTrans;
import com.wt2024.points.core.api.jackson.BigDecimalFormat;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * @ClassName PointsAccountDetailDomain
 * @Description: TODO
 * @Author shade.yang
 * @Date 2021/5/26
 * @Version V1.0
 **/
@Getter
@Setter
public class PointsAccountDetails implements Serializable {

    @BigDecimalFormat
    private BigDecimal pointsAmount;

    @JsonFormat(pattern = Constants.DATE_TIME_FORMATTER_YYYYMMDD_HHMMSS, timezone = Constants.TIMEZONE)
    private Date expirationTime;

    private List<PointsTrans> accountingPointsTrans = new ArrayList<>();

}
