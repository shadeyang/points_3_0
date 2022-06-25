package com.wt2024.points.repository.api.account.repository;

import com.wt2024.points.common.enums.AccountStatus;
import com.wt2024.points.repository.api.account.domain.PointsAccountInfoDTO;

import java.util.List;

/**
 * @author Shade.Yang [shade.yang@aliyun.com]
 * @date 2021/12/16 16:18
 * @project points3.0:com.wt2024.points.repository.api.points.repository
 */
public interface PointsAccountInfoRepository {

    List<PointsAccountInfoDTO> queryPointsAccountInfo(String customersId, String pointTypeNo, String institutionId);

    PointsAccountInfoDTO queryPointsAccountInfoByType(String customersId, String pointTypeNo);

    PointsAccountInfoDTO changePointsAccountStatus(String customersId, String pointTypeNo, AccountStatus status);

    PointsAccountInfoDTO freezePoints(PointsAccountInfoDTO pointsAccountInfo);
}
