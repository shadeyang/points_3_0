package com.wt2024.points.repository.account;

import com.wt2024.points.common.enums.AccountStatus;
import com.wt2024.points.repository.account.converter.ConverterConstants;
import com.wt2024.points.repository.account.entity.PointsAccountInfo;
import com.wt2024.points.repository.account.entity.PointsType;
import com.wt2024.points.repository.account.mapper.PointsAccountInfoMapper;
import com.wt2024.points.repository.account.mapper.PointsTypeMapper;
import com.wt2024.points.repository.api.account.domain.PointsAccountInfoDTO;
import com.wt2024.points.repository.api.account.repository.PointsAccountInfoRepository;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author Shade.Yang [shade.yang@aliyun.com]
 * @date 2021/12/16 17:26
 * @project points3.0:com.wt2024.points.repository.points
 */
@Slf4j
@Repository
public class PointsAccountInfoRepositoryImpl implements PointsAccountInfoRepository {

    @Autowired
    PointsAccountInfoMapper pointsAccountInfoMapper;
    @Autowired
    PointsTypeMapper pointsTypeMapper;

    @Override
    public List<PointsAccountInfoDTO> queryPointsAccountInfo(String customersId, String pointTypeNo, String institutionId) {
        List<String> pointsTypeNoList = new ArrayList<>();
        if(StringUtils.isNotEmpty(institutionId)) {
            List<PointsType> pointsTypeList = pointsTypeMapper.selectByInstitutionId(institutionId);
            pointsTypeNoList = pointsTypeList.stream().map(PointsType::getPointsTypeNo).collect(Collectors.toList());
            if (pointsTypeNoList.isEmpty()) {
                log.info("当前入参机构{}无匹配积分类型", institutionId);
                return Collections.emptyList();
            }

            if (StringUtils.isNotEmpty(pointTypeNo)) {
                if (pointsTypeNoList.contains(pointTypeNo)) {
                    pointsTypeNoList.add(pointTypeNo);
                } else {
                    log.info("当前入参积分类型{}与机构{}不符", pointTypeNo, institutionId);
                    return Collections.emptyList();
                }
            }
        }else{
            if (StringUtils.isNotEmpty(pointTypeNo)) {
                pointsTypeNoList.add(pointTypeNo);
            }
        }

        List<PointsAccountInfo> pointsAccountInfoList = pointsAccountInfoMapper.selectPointsAccountInfoByPointsTypeNo(customersId, pointsTypeNoList);
        if (pointsAccountInfoList.isEmpty() && StringUtils.isNotEmpty(pointTypeNo)) {
            PointsAccountInfo pointsAccountInfo = new PointsAccountInfo(customersId, pointTypeNo, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, AccountStatus.AVAILABLE.getStatus());
            pointsAccountInfoMapper.insert(pointsAccountInfo);
            pointsAccountInfoList.add(pointsAccountInfo);
        }
        return pointsAccountInfoList.stream().map(ConverterConstants.POINTS_ACCOUNT_INFO_CONVERTER::toPointsAccountInfoDTO).collect(Collectors.toList());
    }

    @Override
    public PointsAccountInfoDTO queryPointsAccountInfoByType(String customersId, String pointTypeNo) {
        PointsAccountInfo pointsAccountInfo = getPointsAccountInfoDTO(customersId, pointTypeNo);
        return ConverterConstants.POINTS_ACCOUNT_INFO_CONVERTER.toPointsAccountInfoDTO(Objects.nonNull(pointsAccountInfo)?pointsAccountInfo:(new PointsAccountInfo(customersId,
                pointTypeNo,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                BigDecimal.ZERO,
                AccountStatus.AVAILABLE.getStatus())));
    }

    @Override
    public PointsAccountInfoDTO changePointsAccountStatus(String customersId, String pointTypeNo, AccountStatus status) {
        pointsAccountInfoMapper.updateStatus(customersId,pointTypeNo,status.getStatus());
        return ConverterConstants.POINTS_ACCOUNT_INFO_CONVERTER.toPointsAccountInfoDTO(getPointsAccountInfoDTO(customersId,pointTypeNo));
    }

    private PointsAccountInfo getPointsAccountInfoDTO(String customersId, String pointTypeNo) {
        List<PointsAccountInfo> pointsAccountInfoList = pointsAccountInfoMapper.selectPointsAccountInfoByPointsTypeNo(customersId, Collections.singletonList(pointTypeNo));
        return pointsAccountInfoList.stream().findFirst().orElse(null);
    }

    @Override
    public PointsAccountInfoDTO freezePoints(PointsAccountInfoDTO pointsAccountInfo) {
        pointsAccountInfoMapper.updateFreezingPoints(pointsAccountInfo.getCustomerId(),pointsAccountInfo.getPointsTypeNo(),pointsAccountInfo.getFreezingPoints());
        return ConverterConstants.POINTS_ACCOUNT_INFO_CONVERTER.toPointsAccountInfoDTO(getPointsAccountInfoDTO(pointsAccountInfo.getCustomerId(),pointsAccountInfo.getPointsTypeNo()));
    }
}
