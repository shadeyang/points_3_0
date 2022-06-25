package com.wt2024.base.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.wt2024.base.Constants;
import com.wt2024.base.entity.SysDictionary;
import com.wt2024.base.mapper.SysDictionaryMapper;
import com.wt2024.base.service.ISysDictionaryService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author Shade.Yang [shade.yang@aliyun.com]
 * @Date 2018/8/28 15:49
 * @Project rightsplat:com.wt2024.base.service.impl
 */
@Component
public class SysDictionaryService implements ISysDictionaryService {

    @Autowired
    public SysDictionaryMapper sysDictionaryMapper;

    @Override
    public List<SysDictionary> querySysDictionaryByModule(String module) {
        List<String> modules = new ArrayList<>();
        modules.add(Constants.MODULE_COMMON);
        if(!StringUtils.isEmpty(module)){
            modules.add(module);
        }
        return sysDictionaryMapper.selectByModule(modules);
    }

    @Override
    public List<SysDictionary> querySysDictionary() {
        return sysDictionaryMapper.selectAll();
    }

    @Override
    public SysDictionary saveSysDictionaryByModule(String module, SysDictionary sysDictionary) {
        if(StringUtils.isEmpty(module)){
            module = Constants.MODULE_COMMON;
        }
        sysDictionary.setDictModule(module);
        if(StringUtils.isEmpty(sysDictionary.getDictStatus())){
            sysDictionary.setDictStatus(Constants.AVAILABLE_STATUS);
        }
        if(sysDictionary.getDictIndex()==null) {
            sysDictionary.setDictIndex(0);
        }

        int count = sysDictionaryMapper.selectCountByDictKey(module,sysDictionary.getDictKey(),sysDictionary.getDictLable());
        if(count>0){
            sysDictionaryMapper.updateByDictKey(sysDictionary);
        }else{
            sysDictionaryMapper.saveSysDictionary(sysDictionary);
        }
        return sysDictionary;
    }

    @Override
    public List<SysDictionary> querySysDictionaryByModuleByDB(String module, SysDictionary sysDictionary) {
        List<String> modules = new ArrayList<>();
        modules.add(Constants.MODULE_COMMON);
        if(!StringUtils.isEmpty(module)){
            modules.add(module);
        }
        return sysDictionaryMapper.selectByModuleAndDict(modules,sysDictionary);
    }

    @Override
    public SysDictionary querySysDictionaryByLabelModuleByDB(String module, SysDictionary sysDictionary) {
        List<String> modules = new ArrayList<>();
        modules.add(Constants.MODULE_COMMON);
        if(!StringUtils.isEmpty(module)){
            modules.add(module);
        }
        return sysDictionaryMapper.selectByModuleAndDictLable(modules,sysDictionary);
    }

    @Override
    public void delSysDictionaryByModule(String module, SysDictionary sysDictionary) {
        if(StringUtils.isEmpty(module)){
            module = Constants.MODULE_COMMON;
        }
        sysDictionary.setDictModule(module);
        sysDictionaryMapper.delSysDictionary(sysDictionary);
    }

    @Override
    public IPage<SysDictionary> selectPage(Page<SysDictionary> page, SysDictionary sysDictionary) {
        QueryWrapper<SysDictionary> wrapper = new QueryWrapper<SysDictionary>();
        if(sysDictionary!=null) {
            if (StringUtils.isNotEmpty(sysDictionary.getDictKey())) {
                wrapper.like("dict_key", sysDictionary.getDictKey());
            }
            if(StringUtils.isNotEmpty(sysDictionary.getDictLable())) {
                wrapper.like("dict_lable", sysDictionary.getDictLable());
            }
            if(StringUtils.isNotEmpty(sysDictionary.getDictStatus())) {
                wrapper.eq("dict_status", sysDictionary.getDictStatus());
            }
            if(StringUtils.isNotEmpty(sysDictionary.getDictModule())) {
                wrapper.eq("dict_module", sysDictionary.getDictModule());
            }
            if(StringUtils.isNotEmpty(sysDictionary.getDictDesc())){
                wrapper.like("dict_key", sysDictionary.getDictDesc());
            }
        }
        wrapper.orderByDesc("dict_module","dict_index","dict_key");
        return sysDictionaryMapper.selectPage(page,wrapper);
    }

    @Override
    public SysDictionary querySysDictionaryByDict(SysDictionary sysDictionary) {
        List<String> modules = new ArrayList<>();
        modules.add(sysDictionary.getDictModule());
        return sysDictionaryMapper.selectByModuleAndDictLable(modules,sysDictionary);
    }

}
