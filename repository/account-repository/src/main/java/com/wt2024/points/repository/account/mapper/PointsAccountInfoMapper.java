package com.wt2024.points.repository.account.mapper;

import com.wt2024.points.repository.account.entity.PointsAccountInfo;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;

public interface PointsAccountInfoMapper {
    int insert(PointsAccountInfo record);

    int insertSelective(PointsAccountInfo record);

    List<PointsAccountInfo> selectPointsAccountInfoByPointsTypeNo(@Param("customerId") String customerId, @Param("pointsTypeNos") List<String> pointsTypeNos);

    int updatePointAmount(@Param("customerId") String customerId, @Param("pointsTypeNo") String pointsTypeNo, @Param("pointsAmount") BigDecimal pointsAmount);

    List<PointsAccountInfo> selectPointsAccountInfo(PointsAccountInfo pointsAccountInfo);

    int updateStatus(@Param("customerId") String customerId, @Param("pointsTypeNo") String pointsTypeNo,@Param("status")String status);

    int updateFreezingPoints(@Param("customerId") String customerId, @Param("pointsTypeNo") String pointsTypeNo,@Param("freezingPoints") BigDecimal freezingPoints);
}