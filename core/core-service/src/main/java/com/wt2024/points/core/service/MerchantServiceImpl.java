package com.wt2024.points.core.service;

import com.wt2024.points.common.code.PointsCode;
import com.wt2024.points.common.exception.PointsException;
import com.wt2024.points.core.api.domain.merchant.ClearingList;
import com.wt2024.points.core.api.service.MerchantService;
import com.wt2024.points.core.converter.ConverterConstants;
import com.wt2024.points.repository.api.merchant.domain.MerchantClearingDTO;
import com.wt2024.points.repository.api.merchant.repository.MerchantClearingRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

/**
 * @author Shade.Yang [shade.yang@aliyun.com]
 * @date 2022/6/9 16:59
 * @project points3.0:com.wt2024.points.core.service
 */
@Service
@Validated
@Slf4j
public class MerchantServiceImpl implements MerchantService {

    @Autowired
    MerchantClearingRepository merchantClearingRepository;

    @Override
    public void eventAsyncTrans(ClearingList clearingList) {
        MerchantClearingDTO merchantClearingDTO = ConverterConstants.MERCHANT_CONVERTER.toMerchantClearingDTO(clearingList);
        int result = merchantClearingRepository.createMerchantClearing(merchantClearingDTO);
        if (result <= 0) {
            throw new PointsException(PointsCode.TRANS_2102, "保存清算流水失败");
        }
    }
}
