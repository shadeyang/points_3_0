package com.wt2024.points.core.api.service;

import com.wt2024.points.core.api.domain.account.*;

import javax.validation.Valid;

/**
 * @Author Shade.Yang [shade.yang@aliyun.com]
 * @Date 2021/5/26 15:05
 * @Project points2.0:com.wt2024.points.service.facade
 */
public interface AccountService {

    AccountInfoQueryAllOutput queryAllAccountInfo(@Valid AccountInfoQueryAllInput accountInfoQueryAllInput);

    AccountInfoQueryOutput queryAccountInfoByPointsType(@Valid AccountInfoQueryInput accountInfoQueryInput);

    AccountDetailsQueryOutput queryAccountDetails(@Valid AccountDetailsQueryInput accountDetailsQueryInput);
}
