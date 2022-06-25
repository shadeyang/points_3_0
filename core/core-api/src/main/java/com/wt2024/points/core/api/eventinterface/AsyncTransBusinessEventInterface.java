package com.wt2024.points.core.api.eventinterface;

import com.wt2024.points.core.api.domain.merchant.ClearingList;

/**
 * @author Shade.Yang [shade.yang@aliyun.com]
 * @date 2022/6/9 15:34
 * @project points3.0:com.wt2024.points.core.api.eventinterface
 */
public interface AsyncTransBusinessEventInterface {

    void eventAsyncTrans(ClearingList clearingList);
}
