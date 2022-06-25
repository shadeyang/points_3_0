package com.wt2024.points.core.api.domain.trans;

import com.wt2024.points.core.api.domain.OutputBase;
import com.wt2024.points.core.api.jackson.BigDecimalFormat;
import lombok.*;

import java.math.BigDecimal;

/**
 * @Author Shade.Yang [shade.yang@aliyun.com]
 * @Date 2021/8/16 14:50
 * @Project points2.0:com.wt2024.points.core.api.domain.trans
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PointsAccTransOutput extends OutputBase {
    /**
     * 主机日期
     */
    private String transDate;
    /**
     * 主机流水号
     */
    private String transNo;
    /**
     * 积分余额
     */
    @BigDecimalFormat
    private BigDecimal pointsBalance;
}
