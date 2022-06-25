package com.wt2024.points.repository.api.account.repository;

import com.wt2024.points.repository.api.account.domain.PointsCostDTO;

import java.util.List;

/**
 * @author Shade.Yang [shade.yang@aliyun.com]
 * @date 2022/5/31 09:32
 * @project points3.0:com.wt2024.points.repository.api.account.repository
 */
public interface PointsCostRepository {

    PointsCostDTO queryPointsCostById(String costLine, String institutionId);

    List<PointsCostDTO> queryAllPointsCostByInstitutionId(String institutionId);

    Integer createPointsCost(PointsCostDTO pointsCostDTO);

    Integer updatePointsCost(PointsCostDTO pointsCostDTO);

    Integer deletePointsCost(PointsCostDTO pointsCostDTO);
}
