package com.wt2024.points.repository.account.converter;

import com.wt2024.points.repository.account.entity.PointsType;
import com.wt2024.points.repository.api.account.domain.PointsTypeDTO;
import org.mapstruct.Mapper;

/**
 * @ClassName PointsTypeConverter
 * @Description: TODO
 * @Author shade.yang
 * @Date 2022/1/28
 * @Version V1.0
 **/
@Mapper
public interface PointsTypeConverter {

    PointsTypeDTO toPointsTypeDTO(PointsType pointsType);

    PointsType toPointsType(PointsTypeDTO pointsTypeDTO);
}
