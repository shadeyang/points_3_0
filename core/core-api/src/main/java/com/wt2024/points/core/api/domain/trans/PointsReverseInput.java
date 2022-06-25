package com.wt2024.points.core.api.domain.trans;

import com.wt2024.points.core.api.domain.InputBase;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;

/**
 * @ClassName PointsReverseInput
 * @Description: TODO
 * @Author shade.yang
 * @Date 2021/8/26
 * @Version V1.0
 **/
@Setter
@Getter
public class PointsReverseInput extends InputBase {

    @NotEmpty(message = "发起流水号不能为空")
    private String sysTransNo;

    @NotEmpty(message = "需要冲正的流水号不能为空")
    private String reverseSysTransNo;

    private String operator;
}
