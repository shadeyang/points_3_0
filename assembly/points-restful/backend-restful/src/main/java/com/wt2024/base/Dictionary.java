package com.wt2024.base;

import com.wt2024.base.entity.SysDictionary;
import com.wt2024.base.utils.CacheMachineUtils;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author Shade.Yang [shade.yang@aliyun.com]
 * @Date 2018/8/29 11:04
 * @Project rightsplat:com.wt2024.base
 */
@Component
public class Dictionary {

    public static Map<String,List<SysDictionary>> dictionaryKeyMap = new HashMap<>();

    public static Map<String,SysDictionary> dictionaryKeyLableMap = new HashMap<>();

    /**引入模式**/
    public static List<SysDictionary> getSysDictionary(String dictKey){
        CacheMachineUtils.saveLocalCache();
        return dictionaryKeyMap.get(dictKey);
    }

    /**服务模式**/
    public static List<SysDictionary> getSysDictionaryByModule(String dictKey,String module){
        List<SysDictionary> sysDictionaryList = dictionaryKeyMap.get(dictKey+"@@"+module);
        if(CollectionUtils.isEmpty(sysDictionaryList)){
            sysDictionaryList = dictionaryKeyMap.get(dictKey+"@@"+Constants.MODULE_COMMON);
        }
        return sysDictionaryList;
    }
    /**引入模式**/
    public static SysDictionary getSysDictionary(String dictKey, String dictLabel){
        CacheMachineUtils.saveLocalCache();
        return dictionaryKeyLableMap.get(dictKey+"@@"+dictLabel);
    }
    /**服务模式**/
    public static SysDictionary getSysDictionaryByModule(String dictKey, String dictLabel, String module){
        SysDictionary sysDictionary = null;
        List<SysDictionary> sysDictionaryList = dictionaryKeyMap.get(dictKey+"@@"+module);
        //当前模块无配置，直接获取通用模块配置，当前模块有配置直接获取当前模块结果
        if(CollectionUtils.isEmpty(sysDictionaryList)){
            sysDictionary = dictionaryKeyLableMap.get(dictKey+"@@"+dictLabel+"@@"+Constants.MODULE_COMMON);
        }else{
            sysDictionary = dictionaryKeyLableMap.get(dictKey+"@@"+dictLabel+"@@"+module);
        }
        return sysDictionary;
    }

}
