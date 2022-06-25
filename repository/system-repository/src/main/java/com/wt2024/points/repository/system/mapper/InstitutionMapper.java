package com.wt2024.points.repository.system.mapper;

import com.wt2024.points.repository.system.entity.Institution;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface InstitutionMapper {
    int insert(Institution record);

    int insertSelective(Institution record);

    Institution selectFirstOne(Institution institution);

    List<Institution> selectByTopInstitutionId(@Param("topInstitutionId") String topInstitutionId);

    List<Institution> selectByParentInstitutionId(@Param("parentInstitutionId") String parentInstitutionId);

    Institution selectByInstitutionId(@Param("institutionId") String institutionId);

    Integer updateInfo(Institution institution);

    Integer deleteByInstitutionId(@Param("institutionId")String institutionId);
}