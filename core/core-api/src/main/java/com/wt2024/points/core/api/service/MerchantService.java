package com.wt2024.points.core.api.service;

import com.wt2024.points.core.api.domain.merchant.ClearingList;

import javax.validation.Valid;

/**
 * @author Shade.Yang [shade.yang@aliyun.com]
 * @date 2022/6/9 16:53
 * @project points3.0:com.wt2024.points.core.api.service
 */
public interface MerchantService {

    void eventAsyncTrans(@Valid ClearingList clearingList);
}
