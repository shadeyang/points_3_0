package com.wt2024.points.core.api.domain.account;

import com.wt2024.points.core.api.domain.OutputBase;
import lombok.*;

import java.util.List;

/**
 * @Author Shade.Yang [shade.yang@aliyun.com]
 * @Date 2021/5/26 15:09
 * @Project points2.0:com.wt2024.points.service.domain.account
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AccountInfoQueryAllOutput extends OutputBase {

    private List<PointsAccountInfo> pointsAccountInfoList;

}
