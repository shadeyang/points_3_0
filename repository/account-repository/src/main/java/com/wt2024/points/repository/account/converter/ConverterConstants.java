package com.wt2024.points.repository.account.converter;

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
    public static final PointsTypeConverter POINTS_TYPE_CONVERTER = Mappers.getMapper(PointsTypeConverter.class);
    public static final PointsTransDetailsConverter POINTS_TRANS_DETAILS_CONVERTER = Mappers.getMapper(PointsTransDetailsConverter.class);
    public static final PointsTransExpireConverter POINTS_TRANS_EXPIRE_CONVERTER = Mappers.getMapper(PointsTransExpireConverter.class);

    public static final PointsCostConverter POINTS_COST_CONVERTER = Mappers.getMapper(PointsCostConverter.class);

}
