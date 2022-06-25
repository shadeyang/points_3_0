package com.wt2024.points.repository.api.account.domain;

import lombok.Getter;
import lombok.Setter;

/**
 * @author Shade.Yang [shade.yang@aliyun.com]
 * @date 2022/5/31 09:40
 * @project points3.0:com.wt2024.points.repository.api.account.domain
 */
@Getter
@Setter
public class PointsCostDTO {

    private String costLine;

    private String costName;

    private String institutionId;

}
