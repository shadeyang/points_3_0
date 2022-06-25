package com.wt2024.points.restful.backend.eventinterface;

import com.wt2024.points.core.api.eventinterface.AsyncTransAccountEventInterface;
import com.wt2024.points.core.api.service.TransactionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Shade.Yang [shade.yang@aliyun.com]
 * @date 2022/2/14 11:07
 * @project points3.0:com.wt2024.points.restful.backend.eventinterface
 */
@Service
public class AsyncTransAccountEventInterfaceImpl implements AsyncTransAccountEventInterface {

    @Autowired
    private TransactionService transactionService;

    @Override
    public void eventAsyncTrans(String customerId, String pointsTypeNo) {
        transactionService.eventAsyncTrans(customerId, pointsTypeNo);
    }
}
