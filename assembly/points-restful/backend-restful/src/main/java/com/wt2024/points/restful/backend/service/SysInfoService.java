package com.wt2024.points.restful.backend.service;

import com.wt2024.points.restful.backend.domain.SysInfo;
import com.wt2024.points.restful.backend.entity.SysInfoEntity;
import com.wt2024.points.restful.backend.mapper.SysInfoMapper;
import com.wt2024.points.restful.backend.utils.auth.Base64;
import com.wt2024.points.restful.backend.utils.auth.Des3Tool;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.util.*;
import java.util.stream.Collectors;


/**
 * @ClassName SysInfoService
 * @Description: TODO
 * @Author shade.yang
 * @Date 2021/5/7
 * @Version V1.0
 **/
@Service
@Slf4j
public class SysInfoService {

    @Autowired
    SysInfoMapper sysInfoMapper;

    public List<SysInfo> queryAllSysInfo() {
        List<SysInfoEntity> sysInfoList = sysInfoMapper.selectAll();
        return sysInfoList.stream().map(sysInfo -> convert2SysInfoDomain(sysInfo)).collect(Collectors.toList());
    }

    private SysInfo convert2SysInfoDomain(SysInfoEntity sysInfo) {
        SysInfo sysInfoDomain = new SysInfo();
        sysInfoDomain.setCustaddress(sysInfo.getCustaddress());
        sysInfoDomain.setCustno(sysInfo.getCustno());
        sysInfoDomain.setCustrequestip(sysInfo.getCustrequestip());
        sysInfoDomain.setPublickey(sysInfo.getPublickey());
        String switchconf = sysInfo.getSwitchconf();
        if (!StringUtils.isEmpty(switchconf)) {
            try {
                Map<String, List<String>> switchmap = new HashMap<>();
                String[] item = switchconf.split("\\|");
                for (int i = 0; i < item.length; i++) {
                    String[] unit = item[i].split(":", 2);
                    if (StringUtils.isEmpty(unit[1])) {
                        switchmap.put(unit[0], new ArrayList<String>());
                    } else {
                        switchmap.put(unit[0], Arrays.asList(unit[1].split(",")));
                    }
                }
                sysInfoDomain.setSwitchmap(switchmap);
            } catch (Exception e) {
                log.error("配置解析异常,跳出{}:{}此配置处理 switchconf={}", sysInfo.getInstitution(), sysInfo.getCustno(), switchconf, e);
            }
        }

        if (!StringUtils.isEmpty(sysInfo.getPrivatekey()) && !StringUtils.isEmpty(sysInfo.getThreedeskey())) {
            //解密私钥
            try {
                byte[] encdata = Base64.decode(sysInfo.getPrivatekey().getBytes("UTF-8"));
                byte[] data = Des3Tool.decryptData(sysInfo.getThreedeskey(), encdata);
                sysInfoDomain.setPrivatekey(new String(Base64.encode(data)));
            } catch (UnsupportedEncodingException e) {
                sysInfoDomain.setPrivatekey(null);
                log.error("解密私钥失败", e);
            }
        }
        sysInfoDomain.setType(sysInfo.getType());
        sysInfoDomain.setInstitutionNo(sysInfo.getInstitution());
        return sysInfoDomain;
    }
}
