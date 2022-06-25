package com.wt2024.points.core.api.domain.trans;

import com.wt2024.points.core.api.domain.InputBase;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;

/**
 * @author Shade.Yang [shade.yang@aliyun.com]
 * @date 2021/8/26 18:35
 * @project points2.0:com.wt2024.points.core.api.domain.trans
 */
@Setter
@Getter
public class PointsQueryStateInput extends InputBase {
    @NotEmpty(message = "发起流水号不能为空")
    private String sysTransNo;
}
