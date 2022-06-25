package com.wt2024.base.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wt2024.base.entity.SysMachine;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SysMachineMapper extends BaseMapper<SysMachine> {

    List<SysMachine> selectByModule(@Param("modules") List<String> modules);

    List<SysMachine> selectAll();

    int updateOrInsert(SysMachine sysMachine);

    SysMachine querySysMachine(SysMachine sysMachine);

}