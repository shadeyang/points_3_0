package com.wt2024.points.repository.api.merchant.repository;

import com.wt2024.points.repository.api.merchant.domain.MerchantClearingDTO;

/**
 * @author Shade.Yang [shade.yang@aliyun.com]
 * @date 2022/6/9 16:48
 * @project points3.0:com.wt2024.points.repository.api.merchant.repository
 */
public interface MerchantClearingRepository {

    Integer createMerchantClearing(MerchantClearingDTO merchantClearingDTO);

}
