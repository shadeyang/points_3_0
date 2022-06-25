package com.wt2024.points.repository.merchant;

import com.wt2024.points.repository.api.merchant.domain.MerchantDTO;
import com.wt2024.points.repository.api.merchant.repository.MerchantRepository;
import com.wt2024.points.repository.merchant.converter.ConverterConstants;
import com.wt2024.points.repository.merchant.entity.Merchant;
import com.wt2024.points.repository.merchant.mapper.MerchantMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author Shade.Yang [shade.yang@aliyun.com]
 * @date 2022/2/11 09:55
 * @project points3.0:com.wt2024.points.repository.merchant
 */
@Slf4j
@Repository
public class MerchantRepositoryImpl implements MerchantRepository {

    @Autowired
    MerchantMapper merchantMapper;

    @Override
    public MerchantDTO queryMerchantByMerchantNo(String merchantNo, String institutionId) {
        return ConverterConstants.Merchant_CONVERTER.toMerchantDTO(merchantMapper.selectByMerchantNoAndInstitutionId(merchantNo, institutionId));
    }

    @Override
    public List<MerchantDTO> queryMerchantByInstitutionId(String institutionId) {
        return merchantMapper.selectByInstitutionId(institutionId).stream().map(ConverterConstants.Merchant_CONVERTER::toMerchantDTO).collect(Collectors.toList());
    }

    @Override
    public Integer createMerchant(MerchantDTO merchantDTO, String operator) {
        Merchant merchant = merchantMapper.selectByMerchantNoAndInstitutionId(merchantDTO.getMerchantNo(),merchantDTO.getInstitutionId());
        if(Objects.nonNull(merchant)){
            return 0;
        }
        merchant = ConverterConstants.Merchant_CONVERTER.toMerchant(merchantDTO);
        merchant.setOperator(operator);
        return merchantMapper.insert(merchant);
    }

    @Override
    public Integer updateMerchant(MerchantDTO merchantDTO) {
        Merchant merchant = ConverterConstants.Merchant_CONVERTER.toMerchant(merchantDTO);
        return merchantMapper.updateByMerchantNo(merchant);
    }

    @Override
    public Integer deleteMerchant(MerchantDTO merchant) {
        return merchantMapper.deleteByMerchantNo(merchant.getMerchantNo(), merchant.getInstitutionId());
    }
}
