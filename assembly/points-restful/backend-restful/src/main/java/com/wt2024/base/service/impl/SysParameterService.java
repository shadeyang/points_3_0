package com.wt2024.base.service.impl;

import com.wt2024.base.Constants;
import com.wt2024.base.entity.SysParameter;
import com.wt2024.base.mapper.SysParameterMapper;
import com.wt2024.base.service.ISysParameterService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author Shade.Yang [shade.yang@aliyun.com]
 * @Date 2018/8/29 10:55
 * @Project rightsplat:com.wt2024.base.service.impl
 */
@Component
public class SysParameterService implements ISysParameterService {

    @Autowired
    public SysParameterMapper sysParameterMapper;

    @Override
    public List<SysParameter> querySysParameterByModule(String module) {
        List<String> modules = new ArrayList<>();
        modules.add(Constants.MODULE_COMMON);
        if(!StringUtils.isEmpty(module)){
            modules.add(module);
        }
        return sysParameterMapper.selectByModule(modules);
    }

    @Override
    public List<SysParameter> querySysParameter() {
        return sysParameterMapper.selectAll();
    }

    @Override
    public SysParameter saveSysParameterByModule(String module, SysParameter sysParameter) {
        if(StringUtils.isEmpty(module)){
            module = Constants.MODULE_COMMON;
        }
        sysParameter.setParamModule(module);
        if(StringUtils.isEmpty(sysParameter.getParamStatus())){
            sysParameter.setParamStatus(Constants.AVAILABLE_STATUS);
        }
        int count = sysParameterMapper.selectCountByParamKey(module,sysParameter.getParamKey());
        if(count>0){
            sysParameterMapper.updateByParamKey(sysParameter);
        }else{
            sysParameterMapper.saveSysParameter(sysParameter);
        }
        return sysParameter;
    }

    @Override
    public SysParameter querySysParameterByModuleByDB(String paramKey, String module) {
        List<String> modules = new ArrayList<>();
        modules.add(Constants.MODULE_COMMON);
        if(!StringUtils.isEmpty(module)){
            modules.add(module);
        }
        return sysParameterMapper.selectByModuleByParamKey(modules,paramKey);
    }
}
