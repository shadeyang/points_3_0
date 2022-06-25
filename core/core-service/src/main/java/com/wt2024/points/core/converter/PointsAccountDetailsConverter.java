package com.wt2024.points.core.converter;

import com.wt2024.points.core.api.domain.account.PointsAccountDetails;
import com.wt2024.points.repository.api.account.domain.PointsAccountDetailsDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * @author Shade.Yang [shade.yang@aliyun.com]
 * @date 2021/12/17 17:24
 * @project points3.0:com.wt2024.points.core.converter
 */
@Mapper
public interface PointsAccountDetailsConverter {
    @Mapping(target = "accountingPointsTrans", ignore = true)
    PointsAccountDetails toPointsAccountDetails(PointsAccountDetailsDTO pointsAccountDetailsDTO);
}
