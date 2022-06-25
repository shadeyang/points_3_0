package com.wt2024.base.schedule;

import com.wt2024.base.Constants;
import com.wt2024.base.Dictionary;
import com.wt2024.base.Parameter;
import com.wt2024.base.entity.SysDictionary;
import com.wt2024.base.entity.SysParameter;
import com.wt2024.base.service.ISysDictionaryService;
import com.wt2024.base.service.ISysMachineService;
import com.wt2024.base.service.ISysParameterService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.springframework.util.CollectionUtils;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @Author Shade.Yang [shade.yang@aliyun.com]
 * @Date 2018/8/29 10:02
 * @Project rightsplat:com.wt2024.base.schedule
 */
@Component
public class ScheduledTask {

    private static final Logger logger = LoggerFactory.getLogger(ScheduledTask.class);

    @Value("${spring.application.module}")
    private String moduleName;

    public static String flag = Constants.FLAG_YES; //Y表示原有逻辑，为N表示服务新增逻辑

    @Autowired
    public ISysDictionaryService sysDictionaryService;

    @Autowired
    public ISysParameterService sysParameterService;

    @Autowired
    public ISysMachineService sysMachineService;

    @PostConstruct
    @Scheduled(cron= "0 0/5 * * * ?")
    public void syncDictionary(){
        logger.debug("开始同步字典表数据");

        Map<String,List<SysDictionary>> dictionaryKeyMap = new HashMap<>();
        Map<String,SysDictionary> dictionaryKeyLableMap = new HashMap<>();

        try {

            List<SysDictionary> sysDictionaryList = sysDictionaryService.querySysDictionaryByModule(moduleName);

            sysDictionaryList.stream().forEach(sysDictionary ->{
                List<SysDictionary> sysDictionaries = dictionaryKeyMap.get(sysDictionary.getDictKey());
                if(CollectionUtils.isEmpty(sysDictionaries)){
                    sysDictionaries = new ArrayList<>();
                }
                sysDictionaries.add(sysDictionary);
                dictionaryKeyMap.put(sysDictionary.getDictKey(),sysDictionaries);
            });

            sysDictionaryList.stream().forEach(sysDictionary ->{
                dictionaryKeyLableMap.put(sysDictionary.getDictKey()+"@@"+sysDictionary.getDictLable(),sysDictionary);
            });

            if(!CollectionUtils.isEmpty(dictionaryKeyMap)&&!CollectionUtils.isEmpty(dictionaryKeyLableMap)){
                Dictionary.dictionaryKeyMap=dictionaryKeyMap;
                Dictionary.dictionaryKeyLableMap=dictionaryKeyLableMap;
            }
        } catch (Exception e) {
            logger.error("字典表数据同步信息异常",e);
        }
        logger.debug("字典表数据同步结果,dictionaryKeyMap={},dictionaryKeyLableMap={}",dictionaryKeyMap,dictionaryKeyLableMap);
    }


    @PostConstruct
    @Scheduled(cron= "0 0/10 * * * ?")
    public void syncParameter(){
        logger.debug("开始同步参数表数据");
        Map<String,SysParameter> parameterKeyMap;

        if(Constants.FLAG_YES.equals(flag)){
            List<SysParameter> sysParameterList = sysParameterService.querySysParameterByModule(moduleName);
            parameterKeyMap = sysParameterList.stream().collect(Collectors.toMap(SysParameter::getParamKey, Function.identity(), (key1, key2) -> key2));
        }else{
            List<SysParameter> sysParameterList = sysParameterService.querySysParameter();
            parameterKeyMap = sysParameterList.stream().collect(Collectors.toMap(sysParameter->sysParameter.getParamKey()+"@@"+sysParameter.getParamModule(), Function.identity(), (key1, key2) -> key2));
        }

        if(parameterKeyMap!=null) {
            Parameter.parameterKeyMap = parameterKeyMap;
            logger.debug("字典表数据同步结果,parameterKeyMap={}", parameterKeyMap);
        }else{
            logger.debug("字典表数据同步结果为空，不做更新");
        }
    }

    @Scheduled(initialDelay=60000, fixedRate=60000)
    public void syncMachine(){
        logger.debug("开始同步接入设备数据");
        sysMachineService.heartbeatServerSysMachine();
    }

    public String getFlag() {
        return flag;
    }

    public void setFlag(String flag) {
        ScheduledTask.flag = flag;
    }

}
