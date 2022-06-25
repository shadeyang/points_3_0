package com.wt2024.points.core.api.service;

import com.wt2024.points.common.enums.VoucherType;
import com.wt2024.points.core.api.domain.valid.CustomerInfoValidResult;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * @author Shade.Yang [shade.yang@aliyun.com]
 * @date 2021/12/16 14:29
 * @project points3.0:com.wt2024.points.core.api.service
 */
public interface AggregationService {

    CustomerInfoValidResult checkInputCustomerInfo(@NotNull VoucherType voucherType, @NotEmpty String voucherNo, @NotEmpty String institutionNo);
}
