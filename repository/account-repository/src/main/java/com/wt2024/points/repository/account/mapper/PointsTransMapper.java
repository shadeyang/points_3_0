package com.wt2024.points.repository.account.mapper;

import com.wt2024.points.repository.account.entity.PointsTrans;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface PointsTransMapper {
    int insert(PointsTrans record);

    List<PointsTrans> selectPointsTransByPage(@Param("customerId") String customerId,
                                              @Param("pointsTypeNo") String pointsTypeNo,
                                              @Param("fromId") long fromId,
                                              @Param("fromDate") long fromDate,
                                              @Param("toDate") long toDate,
                                              @Param("startIndex") int startIndex,
                                              @Param("endIndex") int endIndex);

    PointsTrans selectBySysTransNo(@Param("sysTransNo") String sysTransNo);

    List<PointsTrans> selectBySysTransNoAndStatus(@Param("sysTransNo") String sysTransNo, @Param("status") String status);

    List<PointsTrans> selectByOldTransNoAndStatus(@Param("transNo")String transNo, @Param("status") String status);

    List<PointsTrans> selectByTrans(@Param("pointsTrans") PointsTrans pointsTrans, @Param("fromDate") long fromDate,
                       @Param("toDate") long toDate);
}