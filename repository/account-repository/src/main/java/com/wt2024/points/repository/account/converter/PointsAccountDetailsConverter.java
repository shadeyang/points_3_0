package com.wt2024.points.repository.account.converter;

import com.wt2024.points.repository.account.vo.PointsAccountDetails;
import com.wt2024.points.repository.api.account.domain.PointsAccountDetailsDTO;
import org.mapstruct.Mapper;

/**
 * @author Shade.Yang [shade.yang@aliyun.com]
 * @date 2021/12/17 17:24
 * @project points3.0:com.wt2024.points.core.converter
 */
@Mapper
public interface PointsAccountDetailsConverter {

    PointsAccountDetailsDTO toPointsAccountDetailsDTO(PointsAccountDetails pointsAccountDetails);
}
