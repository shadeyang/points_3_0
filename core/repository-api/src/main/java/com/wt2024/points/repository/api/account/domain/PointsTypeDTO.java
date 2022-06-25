package com.wt2024.points.repository.api.account.domain;

import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

/**
 * @author Shade.Yang [shade.yang@aliyun.com]
 * @date 2022/1/26 17:07
 * @project points3.0:com.wt2024.points.repository.api.account.domain
 */
@Getter
@Setter
public class PointsTypeDTO {

    private String pointsTypeNo;

    private String pointsTypeName;

    private String institutionId;

    private BigDecimal rate;

    private String description;
}
