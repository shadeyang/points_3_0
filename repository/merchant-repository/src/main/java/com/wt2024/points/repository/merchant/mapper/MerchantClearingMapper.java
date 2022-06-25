package com.wt2024.points.repository.merchant.mapper;

import com.wt2024.points.repository.merchant.entity.MerchantClearing;

public interface MerchantClearingMapper {
    int insert(MerchantClearing record);

    int insertSelective(MerchantClearing record);
}