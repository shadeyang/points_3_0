package com.wt2024.points.core.api.service;

import com.wt2024.points.core.api.domain.account.PointsAccountExpireInput;
import com.wt2024.points.core.api.domain.trans.*;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

/**
 * @author Shade.Yang [shade.yang@aliyun.com]
 * @date 2022/1/25 16:45
 * @project points3.0:com.wt2024.points.core.api.service
 */
public interface TransactionService {

    PointsTransQueryListOutput queryPointsTransList(@Valid PointsTransQueryListInput pointsTransQueryListInput);

    PointsConsumeOutput consumePoints(@Valid PointsConsumeInput pointsConsumeInput);

    PointsAccTransOutput accTransPoints(@Valid PointsAccTransInput pointsAccTransInput);

    PointsQueryStateOutput queryState(@Valid PointsQueryStateInput pointsQueryStateInput);

    PointsReverseOutput reversePoints(@Valid PointsReverseInput pointsReverseInput);

    PointsBackOutput backPoints(@Valid PointsBackInput pointsBackInput);

    void eventAsyncTrans(@NotEmpty String customerId, @NotEmpty String pointsTypeNo);

    void eventDeleteTransTemp();

    List<PointsAccountExpireInput> queryPointsAccountExpireList(@NotNull Long startId);

    void eventPointsAccountExpire(@Valid PointsAccountExpireInput pointsAccountExpireInput);
}
