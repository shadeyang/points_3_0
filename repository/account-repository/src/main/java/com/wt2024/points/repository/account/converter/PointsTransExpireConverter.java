package com.wt2024.points.repository.account.converter;

import com.wt2024.points.repository.account.entity.PointsTransExpire;
import com.wt2024.points.repository.api.account.domain.PointsExpireDTO;
import org.mapstruct.Mapper;

/**
 * @author Shade.Yang [shade.yang@aliyun.com]
 * @date 2022/2/16 14:10
 * @project points3.0:com.wt2024.points.repository.account.converter
 */
@Mapper
public interface PointsTransExpireConverter {

    PointsExpireDTO toPointsExpireDTO(PointsTransExpire pointsTransExpire);

}
