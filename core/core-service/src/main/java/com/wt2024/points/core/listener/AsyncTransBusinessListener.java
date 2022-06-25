package com.wt2024.points.core.listener;

import com.wt2024.points.core.api.domain.merchant.ClearingList;
import com.wt2024.points.core.api.eventinterface.AsyncTransBusinessEventInterface;
import com.wt2024.points.core.listener.event.AsyncTransBusinessEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationEvent;
import org.springframework.context.event.SmartApplicationListener;
import org.springframework.stereotype.Component;

/**
 * @author Shade.Yang [shade.yang@aliyun.com]
 * @date 2022/2/11 09:28
 * @project points3.0:com.wt2024.points.core.listener
 */
@Component
@Slf4j
public class AsyncTransBusinessListener implements SmartApplicationListener {

    @Autowired
    private AsyncTransBusinessEventInterface asyncTransBusinessEventInterface;

    @Override
    public boolean supportsEventType(Class<? extends ApplicationEvent> eventType) {
        return eventType == AsyncTransBusinessEvent.class;
    }

    @Override
    public void onApplicationEvent(ApplicationEvent event) {
        AsyncTransBusinessEvent businessEvent = (AsyncTransBusinessEvent) event;
        ClearingList clearingList = businessEvent.getClearingList();
        log.info("event merchantNo: {},transNo: {}, clearingAmt: {}", clearingList.getMerchantNo(), clearingList.getTransNo(), clearingList.getClearingAmt());
        asyncTransBusinessEventInterface.eventAsyncTrans(clearingList);
    }

}
