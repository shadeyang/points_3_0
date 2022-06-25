package com.wt2024.points.core.api.domain.trans;

import com.wt2024.points.common.enums.TransStatus;
import com.wt2024.points.core.api.domain.OutputBase;
import lombok.*;

/**
 * @author Shade.Yang [shade.yang@aliyun.com]
 * @date 2021/8/26 18:36
 * @project points2.0:com.wt2024.points.core.api.domain.trans
 */
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PointsQueryStateOutput extends OutputBase {
    /**
     * 主机日期
     */
    private String transDate;
    /**
     * 主机流水号
     */
    private String transNo;
    /**
     * 原交易状态
     */
    private TransStatus transStatus;
}
