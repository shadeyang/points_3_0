package com.wt2024.points.core.api.eventinterface;

import javax.validation.constraints.NotEmpty;

/**
 * @author Shade.Yang [shade.yang@aliyun.com]
 * @date 2022/2/14 11:04
 * @project points3.0:com.wt2024.points.core.api.eventinterface
 */
public interface AsyncTransAccountEventInterface {

    void eventAsyncTrans(@NotEmpty String customerId, @NotEmpty String pointsTypeNo);
}
