package com.wt2024.base.service;

import com.wt2024.base.entity.SysParameter;

import java.util.List;

/**
 * @Author Shade.Yang [shade.yang@aliyun.com]
 * @Date 2018/8/29 10:55
 * @Project rightsplat:com.wt2024.base.service
 */
public interface ISysParameterService {

    List<SysParameter> querySysParameterByModule(String module);

    List<SysParameter> querySysParameter();

    SysParameter saveSysParameterByModule(String module,SysParameter sysParameter);

    SysParameter querySysParameterByModuleByDB(String paramKey, String module);
}
