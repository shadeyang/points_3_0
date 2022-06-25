package com.wt2024.points.restful.backend.mapper;

import com.wt2024.points.restful.backend.entity.SysInfoEntity;

import java.util.List;

public interface SysInfoMapper {
    int insert(SysInfoEntity record);

    int insertSelective(SysInfoEntity record);

    List<SysInfoEntity> selectAll();
}