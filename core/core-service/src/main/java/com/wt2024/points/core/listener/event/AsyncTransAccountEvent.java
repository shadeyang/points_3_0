package com.wt2024.points.core.listener.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

/**
 * @author Shade.Yang [shade.yang@aliyun.com]
 * @date 2022/2/11 09:02
 * @project points3.0:com.wt2024.points.core.listener.event
 */
@Getter
public class AsyncTransAccountEvent extends ApplicationEvent {

    private String customerId;

    private String pointsTypeNo;

    private String transNo;

    public AsyncTransAccountEvent(Object source, String customerId, String pointsTypeNo, String transNo) {
        super(source);
        this.customerId = customerId;
        this.pointsTypeNo = pointsTypeNo;
        this.transNo = transNo;
    }

}
