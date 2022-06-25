package com.wt2024.points.core.api.domain.trans;

import com.wt2024.points.core.api.domain.OutputBase;
import lombok.*;

import java.util.List;

/**
 * @Author Shade.Yang [shade.yang@aliyun.com]
 * @Date 2021/6/4 16:10
 * @Project points2.0:com.wt2024.points.service.domain.trans
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PointsTransQueryListOutput extends OutputBase {
    List<PointsTrans> pointsTransList;
}
