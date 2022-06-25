package com.wt2024.points.core.api.domain.trans;

import com.wt2024.points.core.api.domain.OutputBase;
import lombok.*;

/**
 * @author Shade.Yang [shade.yang@aliyun.com]
 * @date 2022/5/18 16:40
 * @project points3.0:com.wt2024.points.core.api.domain.trans
 */
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PointsBackOutput extends OutputBase {

    /** 主机日期 */
    private String transDate;

    private String backTransNo;
}
