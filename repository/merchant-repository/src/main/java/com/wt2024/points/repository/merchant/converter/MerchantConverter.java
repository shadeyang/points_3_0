package com.wt2024.points.repository.merchant.converter;

import com.wt2024.points.repository.api.merchant.domain.MerchantClearingDTO;
import com.wt2024.points.repository.api.merchant.domain.MerchantDTO;
import com.wt2024.points.repository.merchant.entity.Merchant;
import com.wt2024.points.repository.merchant.entity.MerchantClearing;
import org.mapstruct.Mapper;

/**
 * @author Shade.Yang [shade.yang@aliyun.com]
 * @date 2022/6/1 13:55
 * @project points3.0:com.wt2024.points.repository.merchant.converter
 */
@Mapper
public interface MerchantConverter {

    MerchantDTO toMerchantDTO(Merchant merchant);

    Merchant toMerchant(MerchantDTO merchantDTO);

    MerchantClearing toMerchantClearing(MerchantClearingDTO merchantClearingDTO);
}
