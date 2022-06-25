package com.wt2024.base;

import com.wt2024.base.entity.SysParameter;
import com.wt2024.base.utils.CacheMachineUtils;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;

/**
 * @Author Shade.Yang [shade.yang@aliyun.com]
 * @Date 2018/8/29 12:36
 * @Project rightsplat:com.wt2024.base
 */
@Component
public class Parameter {

    public static Map<String,SysParameter> parameterKeyMap = new HashMap<>();

    /**引入模式**/
    public static SysParameter getSysParameter(String paramKey){
        CacheMachineUtils.saveLocalCache();
        return parameterKeyMap.get(paramKey);
    }
    /**服务模式**/
    public static SysParameter getSysParameterByModule(String paramKey,String module){
        SysParameter sysParameter = parameterKeyMap.get(paramKey+"@@"+module);
        if(sysParameter==null){
            sysParameter = parameterKeyMap.get(paramKey+"@@"+Constants.MODULE_COMMON);
        }
        return sysParameter;
    }
}
