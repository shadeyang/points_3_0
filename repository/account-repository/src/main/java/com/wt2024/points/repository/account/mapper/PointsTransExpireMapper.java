package com.wt2024.points.repository.account.mapper;

import com.wt2024.points.repository.account.entity.PointsTransExpire;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface PointsTransExpireMapper {

    int insert(PointsTransExpire record);

    int insertSelective(PointsTransExpire record);

    int insertBatch(List<PointsTransExpire> insertPointsTransExpireList);

    List<PointsTransExpire> selectPointsTransExpireByPage(
                                              @Param("fromId") long fromId,
                                              @Param("startIndex") int startIndex,
                                              @Param("endIndex") int endIndex);

    List<String> selectPointsExpireByCustomerIdAndPointsTypeNo(@Param("customerId") String customerId, @Param("pointsTypeNo") String pointsTypeNo);

    List<String> selectPointsExpireByTransNoList(@Param("customerId") String customerId, @Param("pointsTypeNo") String pointsTypeNo, @Param("transNoList")List<String> transNoList);

    int deleteByTransNo(@Param("customerId") String customerId, @Param("pointsTypeNo") String pointsTypeNo, @Param("transNoList")List<String> transNoList);
}