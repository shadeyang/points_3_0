package com.wt2024.points.core.listener;

import com.wt2024.points.core.api.eventinterface.AsyncTransAccountEventInterface;
import com.wt2024.points.core.listener.event.AsyncTransAccountEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.event.SmartApplicationListener;
import org.springframework.stereotype.Component;

/**
 * @author Shade.Yang [shade.yang@aliyun.com]
 * @date 2022/2/11 08:57
 * @project points3.0:com.wt2024.points.core.listener
 */

@Component
@Slf4j
public class AsyncTransAccountListener implements SmartApplicationListener {

    @Autowired
    private AsyncTransAccountEventInterface asyncTransAccountEventInterface;

    @Override
    public boolean supportsEventType(Class<? extends ApplicationEvent> eventType) {
        return eventType == AsyncTransAccountEvent.class;
    }

    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        AsyncTransAccountEvent accountEvent = (AsyncTransAccountEvent) event;
        log.info("event customerId: {},pointsTypeNo: {}", accountEvent.getCustomerId(), accountEvent.getPointsTypeNo());
        asyncTransAccountEventInterface.eventAsyncTrans(accountEvent.getCustomerId(),accountEvent.getPointsTypeNo());
    }

}
