package com.wt2024.points.repository.merchant.mapper;

import com.wt2024.points.repository.merchant.entity.Merchant;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface MerchantMapper {
    int insert(Merchant record);

    int insertSelective(Merchant record);

    Merchant selectByMerchantNoAndInstitutionId(@Param("merchantNo") String merchantNo, @Param("institutionId") String institutionId);

    List<Merchant> selectByInstitutionId(@Param("institutionId") String institutionId);

    List<Merchant> selectByMerchant(Merchant merchant);

    Integer updateByMerchantNo(Merchant merchant);

    Integer deleteByMerchantNo(@Param("merchantNo") String merchantNo, @Param("institutionId") String institutionId);
}