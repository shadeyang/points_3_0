package com.wt2024.points.core.service;

import com.wt2024.points.core.api.domain.account.*;
import com.wt2024.points.core.api.domain.trans.PointsTrans;
import com.wt2024.points.core.api.domain.valid.CustomerInfoValidResult;
import com.wt2024.points.core.api.service.AccountService;
import com.wt2024.points.core.converter.ConverterConstants;
import com.wt2024.points.repository.api.account.domain.PointsTransDTO;
import com.wt2024.points.repository.api.account.repository.PointsAccountInfoRepository;
import com.wt2024.points.repository.api.account.repository.PointsTransRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Shade.Yang [shade.yang@aliyun.com]
 * @date 2021/12/15 17:52
 * @project points3.0:com.wt2024.points.core.facade
 */
@Service
@Validated
public class AccountServiceImpl implements AccountService {

    private PointsAccountInfoRepository pointsAccountInfoRepository;

    private PointsTransRepository pointsTransRepository;

    @Override
    public AccountInfoQueryAllOutput queryAllAccountInfo( AccountInfoQueryAllInput accountInfoQueryAllInput) {
        CustomerInfoValidResult customerInfo = accountInfoQueryAllInput.getCustomerInfo();
        List<PointsAccountInfo> pointsAccountInfoList = pointsAccountInfoRepository.queryPointsAccountInfo(customerInfo.getCustomerId(), null, customerInfo.getInstitutionId())
                .stream().map(ConverterConstants.POINTS_ACCOUNT_INFO_CONVERTER::toPointsAccountInfo).collect(Collectors.toList());
        return AccountInfoQueryAllOutput.builder().pointsAccountInfoList(pointsAccountInfoList).build();
    }

    @Override
    public AccountInfoQueryOutput queryAccountInfoByPointsType( AccountInfoQueryInput accountInfoQueryInput) {
        CustomerInfoValidResult customerInfo = accountInfoQueryInput.getCustomerInfo();
        PointsAccountInfo pointsAccountInfo = pointsAccountInfoRepository.queryPointsAccountInfo(customerInfo.getCustomerId(), accountInfoQueryInput.getPointsTypeNo(), customerInfo.getInstitutionId())
                .stream().map(ConverterConstants.POINTS_ACCOUNT_INFO_CONVERTER::toPointsAccountInfo).findFirst().orElseGet(null);
        return AccountInfoQueryOutput.builder().pointsAccountInfo(pointsAccountInfo).build();
    }

    @Override
    public AccountDetailsQueryOutput queryAccountDetails( AccountDetailsQueryInput accountDetailsQueryInput) {
        CustomerInfoValidResult customerInfo = accountDetailsQueryInput.getCustomerInfo();

        List<PointsTransDTO> pointsTransTempList = pointsTransRepository.queryAccountingTrans(customerInfo.getCustomerId(), accountDetailsQueryInput.getPointsTypeNo());
        List<String> accountingTransNo = pointsTransTempList.stream().map(PointsTransDTO::getTransNo).distinct().collect(Collectors.toList());
        List<PointsTrans> accountingTransList = pointsTransTempList.stream().map(ConverterConstants.POINTS_TRANS_CONVERTER::toPointsTrans).collect(Collectors.toList());

        List<PointsAccountDetails> pointsAccountDetailsList = pointsTransRepository.queryPointsAccountDetails(customerInfo.getCustomerId(), accountDetailsQueryInput.getPointsTypeNo(), accountingTransNo)
                .stream().map(ConverterConstants.POINTS_ACCOUNT_DETAILS_CONVERTER::toPointsAccountDetails).collect(Collectors.toList());

        return AccountDetailsQueryOutput.builder().pointsTransList(accountingTransList).pointsAccountDetailsList(pointsAccountDetailsList).build();
    }

    @Autowired
    public void setPointsAccountInfoRepository(PointsAccountInfoRepository pointsAccountInfoRepository) {
        this.pointsAccountInfoRepository = pointsAccountInfoRepository;
    }

    @Autowired
    public void setPointsTransRepository(PointsTransRepository pointsTransRepository) {
        this.pointsTransRepository = pointsTransRepository;
    }
}
