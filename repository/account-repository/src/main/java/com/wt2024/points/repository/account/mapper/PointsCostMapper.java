package com.wt2024.points.repository.account.mapper;

import com.wt2024.points.repository.account.entity.PointsCost;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface PointsCostMapper {
    int insert(PointsCost record);

    int insertSelective(PointsCost record);

    PointsCost selectByCostLine(@Param("costLine") String costLine,@Param("institutionId") String institutionId);

    List<PointsCost> selectByInstitutionId(@Param("institutionId") String institutionId);

    Integer updateByCostLine(PointsCost pointsCost);

    Integer deleteByCostLine(@Param("costLine")String costLine, @Param("institutionId") String institutionId);
}