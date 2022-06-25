package com.wt2024.points.dubbo.backend.facade;

import com.wt2024.points.core.api.domain.account.*;

/**
 * @Author Shade.Yang [shade.yang@aliyun.com]
 * @Date 2021/5/26 15:05
 * @Project points2.0:com.wt2024.points.service.facade
 */
public interface IAccountFacade {

    AccountInfoQueryAllOutput queryAllAccountInfo(AccountInfoQueryAllInput accountInfoQueryAllInput);

    AccountInfoQueryOutput queryAccountInfoByPointsType(AccountInfoQueryInput accountInfoQueryInput);

    AccountDetailsQueryOutput queryAccountDetails(AccountDetailsQueryInput accountDetailsQueryInput);

}
