package com.wt2024.points.repository.account.mapper;

import com.wt2024.points.repository.account.entity.PointsType;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface PointsTypeMapper {
    int insert(PointsType record);

    int insertSelective(PointsType record);

    List<PointsType> selectByInstitutionId(@Param("institutionId") String institutionId);

    PointsType selectByInstitutionAndPointsType(@Param("institutionId") String institutionId, @Param("pointsTypeNo") String pointsTypeNo);

    Integer updateByPointsTypeNo(PointsType pointsType);

    Integer deleteByPointsTypeNo(@Param("pointsTypeNo") String pointsTypeNo,@Param("institutionId") String institutionId);
}