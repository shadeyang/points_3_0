package com.wt2024.points.repository.account.converter;

import com.wt2024.points.repository.account.entity.PointsTransDetails;
import com.wt2024.points.repository.account.entity.PointsTransExpire;
import com.wt2024.points.repository.account.entity.PointsTransTemp;
import com.wt2024.points.repository.account.vo.PointsDetailsBalance;
import com.wt2024.points.repository.api.account.domain.PointsDetailsBalanceDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * @author Shade.Yang [shade.yang@aliyun.com]
 * @date 2022/2/16 10:18
 * @project points3.0:com.wt2024.points.repository.account.converter
 */
@Mapper
public interface PointsTransDetailsConverter {

    @Mapping(source = "transNo", target = "sourceTransNo")
    PointsTransDetails toPointsTransDetails(PointsTransTemp pointsTransTemp);

    @Mapping(source = "sourceTransNo", target = "transNo")
    PointsTransExpire toPointsTransExpire(PointsTransDetails pointsTransDetails);

    PointsDetailsBalanceDTO toPointsDetailsBalanceDTO(PointsDetailsBalance pointsDetailsBalance);
}
