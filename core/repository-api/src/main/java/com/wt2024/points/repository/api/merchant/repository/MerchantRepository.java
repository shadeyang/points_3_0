package com.wt2024.points.repository.api.merchant.repository;

import com.wt2024.points.repository.api.merchant.domain.MerchantDTO;

import java.util.List;

/**
 * @author Shade.Yang [shade.yang@aliyun.com]
 * @date 2022/2/11 09:47
 * @project points3.0:com.wt2024.points.repository.api.merchant.repository
 */
public interface MerchantRepository {

    MerchantDTO queryMerchantByMerchantNo(String merchantNo, String institutionId);

    List<MerchantDTO> queryMerchantByInstitutionId(String institutionId);

    Integer createMerchant(MerchantDTO merchantDTO, String operator);

    Integer updateMerchant(MerchantDTO merchantDTO);

    Integer deleteMerchant(MerchantDTO merchant);
}
