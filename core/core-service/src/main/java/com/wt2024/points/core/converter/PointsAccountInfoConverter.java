package com.wt2024.points.core.converter;

import com.wt2024.points.core.api.domain.account.PointsAccountExpireInput;
import com.wt2024.points.core.api.domain.account.PointsAccountInfo;
import com.wt2024.points.repository.api.account.domain.PointsAccountInfoDTO;
import com.wt2024.points.repository.api.account.domain.PointsExpireDTO;
import org.mapstruct.Mapper;

/**
 * @author Shade.Yang [shade.yang@aliyun.com]
 * @date 2021/12/16 16:27
 * @project points3.0:com.wt2024.points.core.converter
 */
@Mapper
public interface PointsAccountInfoConverter {

    PointsAccountInfo toPointsAccountInfo(PointsAccountInfoDTO pointsAccountInfoDTO);

    PointsAccountExpireInput toPointsAccountExpire(PointsExpireDTO pointsExpireDTO);
}
