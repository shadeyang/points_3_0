package com.wt2024.points.core.converter;

import org.mapstruct.factory.Mappers;

/**
 * @author Shade.Yang [shade.yang@aliyun.com]
 * @date 2021/12/16 16:30
 * @project points3.0:com.wt2024.points.core.converter
 */
public class ConverterConstants {
    public static final PointsAccountInfoConverter POINTS_ACCOUNT_INFO_CONVERTER = Mappers.getMapper(PointsAccountInfoConverter.class);
    public static final PointsTransConverter POINTS_TRANS_CONVERTER = Mappers.getMapper(PointsTransConverter.class);
    public static final PointsAccountDetailsConverter POINTS_ACCOUNT_DETAILS_CONVERTER = Mappers.getMapper(PointsAccountDetailsConverter.class);
    public static final CustomerConverter CUSTOMER_CONVERTER = Mappers.getMapper(CustomerConverter.class);
    public static final InstitutionConverter INSTITUTION_CONVERTER = Mappers.getMapper(InstitutionConverter.class);
    public static final VoucherConverter VOUCHER_CONVERTER = Mappers.getMapper(VoucherConverter.class);
    public static final MerchantConverter MERCHANT_CONVERTER = Mappers.getMapper(MerchantConverter.class);
}
