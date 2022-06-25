package com.wt2024.base.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.wt2024.base.entity.SysDictionary;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface SysDictionaryMapper extends BaseMapper<SysDictionary> {

    List<SysDictionary> selectByModule(@Param("modules") List<String> modules);

    List<SysDictionary> selectAll();

    int selectCountByDictKey(@Param("module")String module, @Param("dictKey")String dictKey, @Param("dictLable")String dictLable);

    int updateByDictKey(SysDictionary sysDictionary);

    int saveSysDictionary(SysDictionary sysDictionary);

    List<SysDictionary> selectByModuleAndDict(@Param("modules")List<String> modules, @Param("sysDictionary")SysDictionary sysDictionary);

    SysDictionary selectByModuleAndDictLable(@Param("modules")List<String> modules, @Param("sysDictionary")SysDictionary sysDictionary);

    int delSysDictionary(SysDictionary sysDictionary);
}