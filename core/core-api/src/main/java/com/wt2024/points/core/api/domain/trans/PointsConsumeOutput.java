package com.wt2024.points.core.api.domain.trans;

import com.wt2024.points.core.api.domain.OutputBase;
import lombok.*;

/**
 * @Author Shade.Yang [shade.yang@aliyun.com]
 * @Date 2021/6/18 16:12
 * @Project points2.0:com.wt2024.points.service.domain.trans
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PointsConsumeOutput extends OutputBase {

    /**
     * 发起系统跟踪号
     */
    private String sysTransNo;
    /**
     * 交易时间
     */
    private String payTime;
    /**
     * 积分支付流水号
     */
    private String pointsTransNo;

}
