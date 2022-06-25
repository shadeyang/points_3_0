package com.wt2024.points.repository.api.account.repository;

import com.wt2024.points.repository.api.account.domain.PointsExpireDTO;

import java.util.List;

/**
 * @author Shade.Yang [shade.yang@aliyun.com]
 * @date 2022/2/16 11:33
 * @project points3.0:com.wt2024.points.repository.api.account.repository
 */
public interface PointsExpireRepository {

    List<PointsExpireDTO> queryCustomerByExpire(long fromId, int index, int pageSize);

    List<String> queryPointsExpireByCustomerIdAndPointsTypeNo(String customerId, String pointsTypeNo);

    int deletePointsExpireWhenBalanceZero(String customerId, String pointsTypeNo, List<String> transNoList);
}
