package com.wt2024.points.repository.api.account.repository;

import com.wt2024.points.repository.api.account.domain.PointsTypeDTO;

import java.util.List;

/**
 * @author Shade.Yang [shade.yang@aliyun.com]
 * @date 2022/1/26 17:05
 * @project points3.0:com.wt2024.points.repository.api.account.repository
 */
public interface PointsTypeRepository {

    PointsTypeDTO queryPointsTypeByInst(String pointsTypeNo, String institutionId);

    List<PointsTypeDTO> queryPointsTypeListByInst(String institutionId);

    Integer createPointsType(PointsTypeDTO pointsTypeDTO);

    Integer updatePointsType(PointsTypeDTO pointsTypeDTO);

    Integer deletePointsType(PointsTypeDTO pointsTypeDTO);
}
