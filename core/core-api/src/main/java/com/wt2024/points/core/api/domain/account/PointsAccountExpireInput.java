package com.wt2024.points.core.api.domain.account;

import com.wt2024.points.core.api.domain.InputBase;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * @author Shade.Yang [shade.yang@aliyun.com]
 * @date 2022/2/16 14:16
 * @project points3.0:com.wt2024.points.core.api.domain.account
 */
@Getter
@Setter
public class PointsAccountExpireInput extends InputBase {

    @NotNull
    private Long id;
    @NotEmpty
    private String customerId;
    @NotEmpty
    private String pointsTypeNo;

}
