package com.wt2024.base.service;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wt2024.base.entity.SysDictionary;

import java.util.List;

/**
 * @Author Shade.Yang [shade.yang@aliyun.com]
 * @Date 2018/8/28 15:47
 * @Project rightsplat:com.wt2024.base.service
 */
public interface ISysDictionaryService {

    List<SysDictionary> querySysDictionaryByModule(String module);

    List<SysDictionary> querySysDictionary();

    SysDictionary saveSysDictionaryByModule(String module,SysDictionary sysDictionary);

    List<SysDictionary> querySysDictionaryByModuleByDB(String module,SysDictionary sysDictionary);

    SysDictionary querySysDictionaryByLabelModuleByDB(String module,SysDictionary sysDictionary);

    void delSysDictionaryByModule(String module,SysDictionary sysDictionary);

    IPage<SysDictionary> selectPage(Page<SysDictionary> page,SysDictionary sysDictionary);

    SysDictionary querySysDictionaryByDict(SysDictionary sysDictionary);

}
