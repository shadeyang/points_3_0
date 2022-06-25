package com.wt2024.points.repository.account.converter;

import com.wt2024.points.repository.account.entity.PointsAccountInfo;
import com.wt2024.points.repository.api.account.domain.PointsAccountInfoDTO;
import org.mapstruct.Mapper;

/**
 * @author Shade.Yang [shade.yang@aliyun.com]
 * @date 2021/12/16 16:27
 * @project points3.0:com.wt2024.points.core.converter
 */
@Mapper
public interface PointsAccountInfoConverter {

    PointsAccountInfoDTO toPointsAccountInfoDTO(PointsAccountInfo pointsAccountInfo);

    PointsAccountInfo toPointsAccountInfo(PointsAccountInfoDTO pointsAccountInfoDTO);

}
