package com.wt2024.points.repository.account.converter;

import com.wt2024.points.repository.account.entity.PointsTrans;
import com.wt2024.points.repository.account.entity.PointsTransDetails;
import com.wt2024.points.repository.account.entity.PointsTransTemp;
import com.wt2024.points.repository.api.account.domain.PointsTransDTO;
import com.wt2024.points.repository.api.account.domain.PointsTransDetailsDTO;
import org.mapstruct.Mapper;

/**
 * @author Shade.Yang [shade.yang@aliyun.com]
 * @date 2021/12/17 17:32
 * @project points3.0:com.wt2024.points.repository.account.converter
 */
@Mapper
public interface PointsTransConverter {
    PointsTransDTO toPointsTransDTO(PointsTransTemp pointsTransTemp);

    PointsTransDTO toPointsTransDTO(PointsTrans pointsTrans);

    PointsTrans toPointsTrans(PointsTransDTO pointsTransDTO);

    PointsTransTemp toPointsTransTemp(PointsTransDTO pointsTransDTO);

    PointsTransDetailsDTO toPointsTransDetailsDTO(PointsTransDetails pointsTransDetails);
}
