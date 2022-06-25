package com.wt2024.points.core.api.validation.object.customerInfo;

import com.wt2024.points.common.enums.VoucherType;
import com.wt2024.points.core.api.domain.InputBase;
import com.wt2024.points.core.api.validation.annotation.CustomerInfoValid;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

/**
 * @author Shade.Yang [shade.yang@aliyun.com]
 * @date 2021/12/16 09:56
 * @project points3.0:com.wt2024.points.core.api.validation.object.customerInfo
 */
@Getter
@Setter
@Builder
@CustomerInfoValid(output = "object")
public class CustomerInfoValidSuccess extends InputBase {
    private String voucherNo;
    private VoucherType voucherType;

    private Object object;
}
