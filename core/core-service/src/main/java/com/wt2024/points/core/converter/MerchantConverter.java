package com.wt2024.points.core.converter;

import com.wt2024.points.common.enums.ReversedFlag;
import com.wt2024.points.core.api.domain.merchant.ClearingList;
import com.wt2024.points.repository.api.account.domain.PointsTransDTO;
import com.wt2024.points.repository.api.merchant.domain.MerchantClearingDTO;
import org.mapstruct.Mapper;

/**
 * @author Shade.Yang [shade.yang@aliyun.com]
 * @date 2022/6/9 15:45
 * @project points3.0:com.wt2024.points.core.converter
 */
@Mapper
public interface MerchantConverter {

    ClearingList toClearingList(PointsTransDTO pointsTransDTO);

    MerchantClearingDTO toMerchantClearingDTO(ClearingList clearingList);

    default ReversedFlag toReversedFlag(String reversedFlag) {
        return ReversedFlag.getEnum(reversedFlag);
    }

    default String toReversedFlag(ReversedFlag reversedFlag) {
        return reversedFlag.getCode();
    }
}
