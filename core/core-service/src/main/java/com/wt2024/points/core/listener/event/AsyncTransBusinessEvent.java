package com.wt2024.points.core.listener.event;

import com.wt2024.points.core.api.domain.merchant.ClearingList;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * @author Shade.Yang [shade.yang@aliyun.com]
 * @date 2022/2/11 09:34
 * @project points3.0:com.wt2024.points.core.listener.event
 */
@Getter
public class AsyncTransBusinessEvent extends ApplicationEvent {

    private ClearingList clearingList;

    public AsyncTransBusinessEvent(Object source, ClearingList clearingList) {
        super(source);
        this.clearingList = clearingList;
    }
}
