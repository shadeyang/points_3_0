package com.wt2024.points.core.api.domain.trans;

import com.wt2024.points.core.api.domain.OutputBase;
import lombok.*;

/**
 * @ClassName PointsReverseOutput
 * @Description: TODO
 * @Author shade.yang
 * @Date 2021/8/26
 * @Version V1.0
 **/
@Setter
@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PointsReverseOutput extends OutputBase {
    /** 主机日期 */
    private String transDate;

    private String reverseTransNo;
}
