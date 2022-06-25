package com.wt2024.points.core.api.domain;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.wt2024.points.core.api.domain.valid.CustomerInfoValidResult;
import com.wt2024.points.core.api.validation.annotation.CustomerInfoValid;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Shade.Yang [shade.yang@aliyun.com]
 * @date 2021/12/16 16:45
 * @project points3.0:com.wt2024.points.core.api.domain.account
 */
@Setter
@Getter
@CustomerInfoValid(output = "customerInfo")
public abstract class CustomerInfoBase extends InputBase {
    @JsonIgnore
    private CustomerInfoValidResult customerInfo;
}
