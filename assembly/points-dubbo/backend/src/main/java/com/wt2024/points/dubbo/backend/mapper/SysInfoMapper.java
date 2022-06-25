package com.wt2024.points.dubbo.backend.mapper;

import com.wt2024.points.dubbo.backend.entity.SysInfoEntity;

import java.util.List;

public interface SysInfoMapper {
    int insert(SysInfoEntity record);

    int insertSelective(SysInfoEntity record);

    List<SysInfoEntity> selectAll();
}