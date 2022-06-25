package com.wt2024.points.repository.api.account.domain;

import lombok.Getter;
import lombok.Setter;

import java.util.Objects;

/**
 * @author Shade.Yang [shade.yang@aliyun.com]
 * @date 2022/2/16 12:58
 * @project points3.0:com.wt2024.points.repository.api.account.domain
 */
@Getter
@Setter
public class PointsExpireDTO {

    private Long id;

    private String customerId;

    private String pointsTypeNo;

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        PointsExpireDTO that = (PointsExpireDTO) o;
        return Objects.equals(customerId, that.customerId) && Objects.equals(pointsTypeNo, that.pointsTypeNo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(customerId, pointsTypeNo);
    }
}
