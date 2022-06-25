package com.wt2024.base.utils;

import com.wt2024.base.config.BaseConfig;
import com.wt2024.base.entity.SysMachine;
import com.wt2024.points.repository.cache.local.CacheManager;
import org.apache.commons.lang3.StringUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

/**
 * @ClassName CacheMachineUtils
 * @Description: TODO
 * @Author shade.yang
 * @Date 2020/6/12
 * @Version V1.0
 **/
public class CacheMachineUtils {

    public static int keepTime = 10*60;

    public static List<String> keys = new ArrayList<>();

    public static void saveLocalCache(){
        String type = "local";
        SysMachine sysMachine = new SysMachine();
        sysMachine.setModule(BaseConfig.moduleName);
        sysMachine.setMachine(BaseConfig.machine);
        sysMachine.setLasttime(curTime());
        sysMachine.setType(type);
        sysMachine.setDescribe("调用自动记录");
        String key = BaseConfig.moduleName+"_"+BaseConfig.machine+"_"+type;
        keys.add(key);
        CacheManager.setData(key,sysMachine,2*keepTime);
    }

    public static void saveServiceCache(String machine,String module,String type){
        if(StringUtils.isEmpty(type)) {
            type = "service";
        }
        SysMachine sysMachine = new SysMachine();
        sysMachine.setModule(module);
        sysMachine.setMachine(machine);
        sysMachine.setLasttime(curTime());
        sysMachine.setType(type);
        sysMachine.setDescribe("调用自动记录");
        String key = module+"_"+machine+"_"+type;
        keys.add(key);
        CacheManager.setData(key,sysMachine,2*keepTime);
    }

    public static SysMachine genMachine(){
        String type = "local";
        SysMachine sysMachine = new SysMachine();
        sysMachine.setModule(BaseConfig.moduleName);
        sysMachine.setMachine(BaseConfig.machine);
        sysMachine.setLasttime(curTime());
        sysMachine.setType(type);
        sysMachine.setNode("server");
        sysMachine.setDescribe("服务启动注册");
        return sysMachine;
    }

    public static String curTime() {
        Calendar now = Calendar.getInstance(TimeZone.getDefault());
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        sdf.setTimeZone(TimeZone.getDefault());
        return sdf.format(now.getTime());
    }

}
