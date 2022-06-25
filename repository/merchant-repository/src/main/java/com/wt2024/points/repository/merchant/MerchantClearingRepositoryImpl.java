package com.wt2024.points.repository.merchant;

import com.wt2024.points.repository.api.merchant.domain.MerchantClearingDTO;
import com.wt2024.points.repository.api.merchant.repository.MerchantClearingRepository;
import com.wt2024.points.repository.merchant.converter.ConverterConstants;
import com.wt2024.points.repository.merchant.mapper.MerchantClearingMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

/**
 * @author Shade.Yang [shade.yang@aliyun.com]
 * @date 2022/6/9 16:48
 * @project points3.0:com.wt2024.points.repository.merchant
 */
@Slf4j
@Repository
public class MerchantClearingRepositoryImpl implements MerchantClearingRepository {

    @Autowired
    MerchantClearingMapper merchantClearingMapper;
    @Override
    public Integer createMerchantClearing(MerchantClearingDTO merchantClearingDTO) {
        return merchantClearingMapper.insert(ConverterConstants.Merchant_CONVERTER.toMerchantClearing(merchantClearingDTO));
    }
}
