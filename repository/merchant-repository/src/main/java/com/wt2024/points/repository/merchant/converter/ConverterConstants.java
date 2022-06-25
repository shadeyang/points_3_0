package com.wt2024.points.repository.merchant.converter;

import org.mapstruct.factory.Mappers;

/**
 * @author Shade.Yang [shade.yang@aliyun.com]
 * @date 2021/12/16 16:30
 * @project points3.0:com.wt2024.points.core.converter
 */
public class ConverterConstants {
    public static final MerchantConverter Merchant_CONVERTER = Mappers.getMapper(MerchantConverter.class);
}
