package com.wt2024.points.restful.backend.eventinterface;

import com.wt2024.points.core.api.domain.merchant.ClearingList;
import com.wt2024.points.core.api.eventinterface.AsyncTransBusinessEventInterface;
import com.wt2024.points.core.api.service.MerchantService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * @author Shade.Yang [shade.yang@aliyun.com]
 * @date 2022/6/9 16:24
 * @project points3.0:com.wt2024.points.restful.backend.eventinterface
 */
@Service
public class AsyncTransBusinessEventInterfaceImpl implements AsyncTransBusinessEventInterface {
    @Autowired
    private MerchantService merchantService;
    @Override
    public void eventAsyncTrans(ClearingList clearingList) {
        merchantService.eventAsyncTrans(clearingList);
    }
}
