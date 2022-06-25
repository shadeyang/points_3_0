package com.wt2024.points.repository.account.converter;

import com.wt2024.points.repository.account.entity.PointsCost;
import com.wt2024.points.repository.api.account.domain.PointsCostDTO;
import org.mapstruct.Mapper;

/**
 * @author Shade.Yang [shade.yang@aliyun.com]
 * @date 2022/5/31 14:09
 * @project points3.0:com.wt2024.points.repository.account.converter
 */
@Mapper
public interface PointsCostConverter {
    PointsCostDTO toPointsCostDTO(PointsCost pointsCost);

    PointsCost toPointsCost(PointsCostDTO pointsCostDTO);
}
