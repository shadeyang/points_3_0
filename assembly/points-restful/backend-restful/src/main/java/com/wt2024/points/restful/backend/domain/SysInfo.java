package com.wt2024.points.restful.backend.domain;

import lombok.Getter;
import lombok.Setter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author Shade.Yang [shade.yang@aliyun.com]
 * @Date 2019-01-08 20:11
 * @Project openapi:com.wt2024.points.domain
 */
@Getter
@Setter
public class SysInfo extends Base {

    private String custno;

    private String type;

    private String custaddress;

    private String custrequestip;

    private String privatekey;

    private String publickey;

    private String threedeskey;

    private String switchconf;

    private Map<String, List<String>> switchmap = new HashMap<>();

    /**
     * 获取配置
     *
     * @param model
     * @return 返回为null和空list为不同概念 为null表示无此配置，全量否定；为list表示有此配置，但是全量通过，所以需要区别对待
     */
    public List<String> getModelSwitch(String model) {
        return this.switchmap.get(model);
    }
}
