package com.wt2024.points.core.api.service;

import com.wt2024.points.core.api.domain.voucher.AddVoucherInput;

import javax.validation.Valid;

/**
 * @author Shade.Yang [shade.yang@aliyun.com]
 * @date 2022/1/25 10:21
 * @project points3.0:com.wt2024.points.core.api.service
 */
public interface VoucherService {

    void addVoucher(@Valid AddVoucherInput addVoucherInput);
}
