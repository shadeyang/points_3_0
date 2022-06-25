package com.wt2024.points.repository.api.merchant.domain;

import lombok.Getter;
import lombok.Setter;

/**
 * @author Shade.Yang [shade.yang@aliyun.com]
 * @date 2022/2/11 09:47
 * @project points3.0:com.wt2024.points.repository.api.merchant.domain
 */
@Getter
@Setter
public class MerchantDTO {

    private String merchantNo;

    private String merchantName;

    private String institutionId;

    private String contacts;

    private String phone;

    private String address;

    private String status;

}
