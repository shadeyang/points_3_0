package com.wt2024.base.mapper;

import com.wt2024.base.entity.SysParameter;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SysParameterMapper{

    List<SysParameter> selectByModule(@Param("modules") List<String> modules);

    List<SysParameter> selectAll();

    int selectCountByParamKey(@Param("module") String module,@Param("paramKey") String paramKey);

    int updateByParamKey(SysParameter sysParameter);

    int saveSysParameter(SysParameter sysParameter);

    SysParameter selectByModuleByParamKey(@Param("modules")List<String> modules, @Param("paramKey")String paramKey);
}