package com.wt2024.points.repository.account;

import com.wt2024.points.common.Constants;
import com.wt2024.points.common.code.PointsCode;
import com.wt2024.points.common.enums.*;
import com.wt2024.points.common.exception.PointsException;
import com.wt2024.points.repository.account.converter.ConverterConstants;
import com.wt2024.points.repository.account.entity.*;
import com.wt2024.points.repository.account.mapper.*;
import com.wt2024.points.repository.api.account.domain.PointsAccountDetailsDTO;
import com.wt2024.points.repository.api.account.domain.PointsDetailsBalanceDTO;
import com.wt2024.points.repository.api.account.domain.PointsTransDTO;
import com.wt2024.points.repository.api.account.domain.PointsTransDetailsDTO;
import com.wt2024.points.repository.api.account.repository.PointsTransRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.CollectionUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;

/**
 * @author Shade.Yang [shade.yang@aliyun.com]
 * @date 2021/12/17 17:28
 * @project points3.0:com.wt2024.points.repository.account
 */
@Slf4j
@Repository
public class PointsTransRepositoryImpl implements PointsTransRepository {

    private static final String GEN_POINTS_TRANS_DETAIL_TYPE_REVERSED = "REVERSED";
    private static final String GEN_POINTS_TRANS_DETAIL_TYPE_BACK = "BACK";
    private static final String GEN_POINTS_TRANS_DETAIL_TYPE_DEBIT = "DEBIT";

    @Autowired
    PointsTransTempMapper pointsTransTempMapper;
    @Autowired
    PointsTransDetailsMapper pointsTransDetailsMapper;
    @Autowired
    PointsTypeMapper pointsTypeMapper;
    @Autowired
    PointsTransMapper pointsTransMapper;
    @Autowired
    PointsAccountInfoMapper pointsAccountInfoMapper;
    @Autowired
    PointsTransExpireMapper pointsTransExpireMapper;

    @Override
    public List<PointsTransDTO> queryAccountingTrans(String customerId, String pointsTypeNo) {
        return pointsTransTempMapper.queryCustomerAccountingPointsTrans(customerId, pointsTypeNo).stream().map(ConverterConstants.POINTS_TRANS_CONVERTER::toPointsTransDTO).collect(Collectors.toList());
    }

    @Override
    public List<PointsAccountDetailsDTO> queryPointsAccountDetails(String customerId, String pointsTypeNo, List<String> accountingTransNo) {
        return pointsTransDetailsMapper.queryPointsAccountDetail(customerId, pointsTypeNo, accountingTransNo).stream().map(ConverterConstants.POINTS_ACCOUNT_DETAILS_CONVERTER::toPointsAccountDetailsDTO).collect(Collectors.toList());
    }

    @Override
    public List<PointsTransDTO> queryPointsTransByPage(String customerId, String institutionId, String pointsTypeNo, long fromId, Date fromDate, Date toDate, int index, int pageSize) {
        PointsType pointsType = pointsTypeMapper.selectByInstitutionAndPointsType(institutionId, pointsTypeNo);
        if (Objects.isNull(pointsType)) {
            log.info("??????????????????{}???????????????{}?????????", institutionId, pointsTypeNo);
            return Collections.emptyList();
        }
        index = index == 0 ? Constants.PAGE.DEFAULT_INDEX : index;
        pageSize = pageSize == 0 ? Constants.PAGE.DEFAULT_PAGE_SIZE : pageSize;
        long fromTime = fromDate == null ? -1 : fromDate.getTime();
        long toTime = toDate == null ? System.currentTimeMillis() : toDate.getTime();
        List<PointsTrans> pointsTrans = pointsTransMapper.selectPointsTransByPage(customerId, pointsTypeNo, fromId, fromTime, toTime, pageSize * (index - 1), pageSize * index);
        return pointsTrans.stream().map(ConverterConstants.POINTS_TRANS_CONVERTER::toPointsTransDTO).collect(Collectors.toList());
    }

    @Override
    public PointsTransDTO existsPointsBySysTransNo(String sysTransNo) {
        return ConverterConstants.POINTS_TRANS_CONVERTER.toPointsTransDTO(pointsTransMapper.selectBySysTransNo(sysTransNo));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void processTrans(PointsTransDTO pointsTrans, PointsTransDTO expirePointsTrans) {
        if (TransStatus.SUCCESS.getCode().equals(pointsTrans.getTransStatus())) {
            String customerId = pointsTrans.getCustomerId();
            String pointsTypeNo = pointsTrans.getPointsTypeNo();
            int result = -1;
            //??????????????????
            BigDecimal pointsAmount = pointsTrans.getPointsAmount();
            if (DebitOrCredit.DEBIT.getCode().equals(pointsTrans.getDebitOrCredit())) {
                if (ReversedFlag.NOT_REVERSED.getCode().equals(pointsTrans.getReversedFlag())) {
                    pointsAmount = pointsAmount.negate();
                } else if (ReversedFlag.REVERSED.getCode().equals(pointsTrans.getReversedFlag())) {
                    if (expirePointsTrans != null) {
                        pointsAmount = pointsAmount.subtract(expirePointsTrans.getPointsAmount());
                    }
                }
            } else if (DebitOrCredit.CREDIT.getCode().equals(pointsTrans.getDebitOrCredit())) {
                if (ReversedFlag.NOT_REVERSED.getCode().equals(pointsTrans.getReversedFlag())) {
                    //????????????????????????????????????????????????
                    List<PointsAccountInfo> pointsAccountInfoList = pointsAccountInfoMapper.selectPointsAccountInfoByPointsTypeNo(pointsTrans.getCustomerId(), Arrays.asList(pointsTrans.getPointsTypeNo()));
                    if (pointsAccountInfoList.isEmpty()) {
                        PointsAccountInfo pointsAccountInfo = new PointsAccountInfo(customerId, pointsTypeNo, pointsAmount, BigDecimal.ZERO, BigDecimal.ZERO, AccountStatus.AVAILABLE.getStatus());
                        result = pointsAccountInfoMapper.insert(pointsAccountInfo);
                    }
                } else if (ReversedFlag.REVERSED.getCode().equals(pointsTrans.getReversedFlag())) {
                    pointsAmount = pointsAmount.negate();
                } else if (ReversedFlag.BACKED.getCode().equals(pointsTrans.getReversedFlag())) {
                    if (expirePointsTrans != null) {
                        pointsAmount = pointsAmount.subtract(expirePointsTrans.getPointsAmount());
                    }
                }
            }

            if (result == -1) {
                result = pointsAccountInfoMapper.updatePointAmount(customerId, pointsTypeNo, pointsAmount);
            }
            if (result != 1) {
                throw new PointsException(PointsCode.TRANS_2010);
            }
            pointsTransTempMapper.insert(this.convert2PointsTransTemp(pointsTrans));
            pointsTransMapper.insert(ConverterConstants.POINTS_TRANS_CONVERTER.toPointsTrans(pointsTrans));
            if (expirePointsTrans != null) {
                pointsTransTempMapper.insert(this.convert2PointsTransTemp(expirePointsTrans));
                pointsTransMapper.insert(ConverterConstants.POINTS_TRANS_CONVERTER.toPointsTrans(expirePointsTrans));
            }
        } else {
            pointsTransMapper.insert(ConverterConstants.POINTS_TRANS_CONVERTER.toPointsTrans(pointsTrans));
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void expireProcessTrans(PointsTransDTO expirePointsTrans) {
        this.processTrans(expirePointsTrans, null);
        pointsTransExpireMapper.deleteByTransNo(expirePointsTrans.getCustomerId(), expirePointsTrans.getPointsTypeNo(), Arrays.asList(expirePointsTrans.getOldTransNo()));
    }

    @Override
    public List<PointsTransDTO> queryPointsBySysTransNoAndStatus(String sysTransNo, String status) {
        return pointsTransMapper.selectBySysTransNoAndStatus(sysTransNo, status).stream().map(ConverterConstants.POINTS_TRANS_CONVERTER::toPointsTransDTO).collect(Collectors.toList());
    }

    @Override
    public boolean existsAccountingTrans(String customerId, String pointsTypeNo, String transNo, boolean include) {
        return Objects.nonNull(pointsTransTempMapper.selectOneCustomerAccountingPointsTrans(customerId, pointsTypeNo, transNo, include));
    }

    @Override
    public List<PointsTransDetailsDTO> queryPointsTransDetailsByTransNo(String transNo) {
        return pointsTransDetailsMapper.selectByTransNo(transNo).stream().map(ConverterConstants.POINTS_TRANS_CONVERTER::toPointsTransDetailsDTO).collect(Collectors.toList());
    }

    @Override
    public List<PointsTransDetailsDTO> queryPointsTransDetailsBySourceTransNo(String transNo) {
        return pointsTransDetailsMapper.selectBySourceTransNo(transNo).stream().map(ConverterConstants.POINTS_TRANS_CONVERTER::toPointsTransDetailsDTO).collect(Collectors.toList());
    }

    @Override
    public List<PointsTransDTO> queryAllWantToAsync(String customerId, String pointsTypeNo, Long startIndex, Integer pageSize) {
        return pointsTransTempMapper.queryAllWantToAsync(customerId, pointsTypeNo, startIndex, pageSize).stream().map(ConverterConstants.POINTS_TRANS_CONVERTER::toPointsTransDTO).collect(Collectors.toList());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void processAsyncTrans(PointsTransDTO transDTO) {
        Long id = transDTO.getId();
        PointsTransTemp transTemp = ConverterConstants.POINTS_TRANS_CONVERTER.toPointsTransTemp(transDTO);

        if (pointsTransTempMapper.processUnDoPointsTrans(id) <= 0) {
            log.info("?????????" + transTemp.getTransNo() + "[" + id + "]????????????????????????????????????");
            return;
        }

        BigDecimal pointsAmount = DebitOrCredit.CREDIT.getCode().equals(transTemp.getDebitOrCredit()) ? transTemp.getPointsAmount() : transTemp.getPointsAmount().negate();
        List<PointsTransDetails> insertPointsTransDetailsList = new ArrayList<>();
        List<PointsTransExpire> insertPointsTransExpireList = new ArrayList<>();

        if (ReversedFlag.REVERSED.getCode().equals(transTemp.getReversedFlag())) {
            //???????????????????????????????????????????????????????????????
            //???????????????
            List<PointsTransDetails> pointsTransCanReversedDetailsList = pointsTransDetailsMapper.selectByTransNo(transTemp.getOldTransNo());
            pointsAmount = pointsAmount.negate();
            BigDecimal clearingAmt = transTemp.getClearingAmt().negate();
            insertPointsTransDetailsList = genPointsTransDetailsList(pointsTransCanReversedDetailsList, transTemp, pointsAmount, clearingAmt, GEN_POINTS_TRANS_DETAIL_TYPE_REVERSED);
            if (DebitOrCredit.DEBIT.getCode().equals(transTemp.getDebitOrCredit())) {
                insertPointsTransExpireList = getPointsTransExpires(transTemp, insertPointsTransDetailsList);
            }
        } else if (ReversedFlag.BACKED.getCode().equals(transTemp.getReversedFlag())) {
            // ????????????????????????????????????
            List<PointsTrans> oldTransNoPointsTransList = pointsTransMapper.selectByOldTransNoAndStatus(transTemp.getOldTransNo(), TransStatus.SUCCESS.getCode())
                    .stream().sorted(Comparator.comparing(PointsTrans::getId)).collect(Collectors.toList());
            List<String> transNoList = oldTransNoPointsTransList.stream().map(PointsTrans::getTransNo).collect(Collectors.toList());
            //??????????????????????????????????????????
            List<PointsTransDetails> pointsTransDetailsList = pointsTransDetailsMapper.selectBackPointsTransDetailsByTransNo(transNoList);
            insertPointsTransDetailsList = genPointsTransDetailsList(pointsTransDetailsList, transTemp, pointsAmount, transTemp.getClearingAmt(), GEN_POINTS_TRANS_DETAIL_TYPE_BACK);
            insertPointsTransExpireList = getPointsTransExpires(transTemp, insertPointsTransDetailsList);
        } else if (DebitOrCredit.CREDIT.getCode().equals(transTemp.getDebitOrCredit())) {
            //??????????????????????????????????????????
            //??????????????????
            PointsTransDetails pointsTransDetails = ConverterConstants.POINTS_TRANS_DETAILS_CONVERTER.toPointsTransDetails(transTemp);
            insertPointsTransDetailsList.add(pointsTransDetails);
            insertPointsTransExpireList.add(ConverterConstants.POINTS_TRANS_DETAILS_CONVERTER.toPointsTransExpire(pointsTransDetails));
        } else if (DebitOrCredit.DEBIT.getCode().equals(transTemp.getDebitOrCredit())) {
            //??????????????????????????????????????????
            //??????????????????????????????????????????
            List<PointsTransDetails> pointsTransDetailsList = pointsTransDetailsMapper.queryPointsTransDetailsByCustomerAndPointType(transTemp.getCustomerId(), transTemp.getPointsTypeNo());
            BigDecimal clearingAmt = transTemp.getClearingAmt();
            insertPointsTransDetailsList = genPointsTransDetailsList(pointsTransDetailsList, transTemp, pointsAmount, clearingAmt, GEN_POINTS_TRANS_DETAIL_TYPE_DEBIT);
        } else {
            log.info("??????????????????: {}", transTemp.getTransNo());
            throw new PointsException(PointsCode.TRANS_2101, "??????????????????:" + transTemp.getTransNo());
        }
        //??????????????????
        if (!CollectionUtils.isEmpty(insertPointsTransExpireList)) {
            pointsTransExpireMapper.insertBatch(insertPointsTransExpireList);
        }
        //????????????????????????
        pointsTransDetailsMapper.insertBatch(insertPointsTransDetailsList);
        if (pointsTransTempMapper.processDonePointsTrans(id) <= 0) {
            log.info("?????????" + id + "?????????????????????????????????");
            throw new PointsException(PointsCode.TRANS_2101, "?????????" + transTemp.getTransNo() + "[" + id + "]?????????????????????????????????");
        }
    }

    private List<PointsTransExpire> getPointsTransExpires(PointsTransTemp transTemp, List<PointsTransDetails> insertPointsTransDetailsList) {
        List<PointsTransExpire> insertPointsTransExpireList;
        insertPointsTransExpireList = insertPointsTransDetailsList.stream().map(ConverterConstants.POINTS_TRANS_DETAILS_CONVERTER::toPointsTransExpire).collect(Collectors.toList());
        List<String> existsExpire = pointsTransExpireMapper.selectPointsExpireByTransNoList(transTemp.getCustomerId(), transTemp.getPointsTypeNo(), insertPointsTransExpireList.stream().map(PointsTransExpire::getTransNo).collect(Collectors.toList()));
        log.info("?????????????????????????????? {}", existsExpire);
        insertPointsTransExpireList = insertPointsTransExpireList.stream().filter(expire -> !existsExpire.contains(expire.getTransNo())).collect(Collectors.toList());
        return insertPointsTransExpireList;
    }

    @Override
    public List<PointsTransDTO> queryPointsByOldTransNoAndStatus(String transNo, String status) {
        return pointsTransMapper.selectByOldTransNoAndStatus(transNo, status).stream().map(ConverterConstants.POINTS_TRANS_CONVERTER::toPointsTransDTO).collect(Collectors.toList());
    }

    @Override
    public int deletePointsTempHasDone() {
        return pointsTransTempMapper.deleteHasDone();
    }

    @Override
    public List<PointsDetailsBalanceDTO> queryPointsDetailsBalanceByTransNo(String customerId, String pointsTypeNo, List<String> transNoList) {
        return pointsTransDetailsMapper.selectPointsDetailsBalanceByTransNo(customerId, pointsTypeNo, transNoList).stream().map(ConverterConstants.POINTS_TRANS_DETAILS_CONVERTER::toPointsDetailsBalanceDTO).collect(Collectors.toList());
    }

    @Override
    public List<PointsTransDetailsDTO> queryBackPointsTransDetailsByTransNo(List<String> transNoList) {
        return pointsTransDetailsMapper.selectBackPointsTransDetailsByTransNo(transNoList).stream().map(ConverterConstants.POINTS_TRANS_CONVERTER::toPointsTransDetailsDTO).collect(Collectors.toList());
    }

    private List<PointsTransDetails> genPointsTransDetailsList(List<PointsTransDetails> pointsTransDetailsList, PointsTransTemp pointsTransTemp, BigDecimal pointsAmount, BigDecimal clearingAmt, String genType) {
        List<PointsTransDetails> genPointsTransDetailsList = new ArrayList();
        //??????????????????
        for (PointsTransDetails pointsTransDetails : pointsTransDetailsList) {
            if (BigDecimal.ZERO.compareTo(pointsTransDetails.getClearingAmt()) == 0 && BigDecimal.ZERO.compareTo(pointsTransDetails.getPointsAmount()) == 0) {
                continue;
            }
            pointsTransDetails.setTransNo(pointsTransTemp.getTransNo());
            pointsTransDetails.setMerchantNo(pointsTransTemp.getMerchantNo());
            BigDecimal oldTransPointsAmount = pointsTransDetails.getPointsAmount();
            int compare = this.compareSubEnough(pointsAmount, oldTransPointsAmount);
            if (compare == 0) {
                pointsTransDetails.setPointsAmount(pointsTransDetails.getPointsAmount().negate());
                if (GEN_POINTS_TRANS_DETAIL_TYPE_REVERSED.equals(genType)) {
                    pointsTransDetails.setClearingAmt(pointsTransDetails.getClearingAmt().negate());
                } else if (GEN_POINTS_TRANS_DETAIL_TYPE_DEBIT.equals(genType) || GEN_POINTS_TRANS_DETAIL_TYPE_BACK.equals(genType)) {
                    pointsTransDetails.setClearingAmt(clearingAmt);
                }
            } else if (compare > 0) {
                pointsTransDetails.setPointsAmount(pointsTransDetails.getPointsAmount().negate());
                if (GEN_POINTS_TRANS_DETAIL_TYPE_REVERSED.equals(genType) || GEN_POINTS_TRANS_DETAIL_TYPE_BACK.equals(genType)) {
                    pointsTransDetails.setClearingAmt(pointsTransDetails.getClearingAmt().negate());
                } else if (GEN_POINTS_TRANS_DETAIL_TYPE_DEBIT.equals(genType)) {
                    pointsTransDetails.setClearingAmt(clearingAmt.multiply(pointsTransDetails.getPointsAmount()).divide(pointsAmount, 2, RoundingMode.DOWN));
                }
            } else {
                pointsTransDetails.setPointsAmount(pointsAmount);
                pointsTransDetails.setClearingAmt(clearingAmt);
            }

            genPointsTransDetailsList.add(pointsTransDetails);
            pointsAmount = pointsAmount.subtract(pointsTransDetails.getPointsAmount());
            clearingAmt = clearingAmt.subtract(pointsTransDetails.getClearingAmt());

            if (compare <= 0) {
                break;
            }
        }

        if (pointsAmount.compareTo(BigDecimal.ZERO) != 0 || clearingAmt.compareTo(BigDecimal.ZERO) != 0) {
            log.error("?????????????????? transNo= " + pointsTransTemp.getTransNo() + ",??????pointsAmount= " + pointsAmount + "??????,clearingAmt=" + clearingAmt + "????????????????????????");
            throw new PointsException(PointsCode.TRANS_2101, "?????????????????? transNo= " + pointsTransTemp.getTransNo());
        }
        return genPointsTransDetailsList;
    }

    /**
     * ??????left?????????????????????right????????????
     *
     * @param left
     * @param right
     * @return -1,0 or 1 as not enough,equal,enough
     */
    private int compareSubEnough(BigDecimal left, BigDecimal right) {
        int compareZero = left.compareTo(BigDecimal.ZERO);
        if (compareZero == 0) {
            if (right.compareTo(BigDecimal.ZERO) == 0) {
                return 0;
            } else {
                return -1;
            }
        }
        return left.add(right).compareTo(BigDecimal.ZERO) * compareZero;
    }

    private PointsTransTemp convert2PointsTransTemp(PointsTransDTO pointsTrans) {
        PointsTransTemp pointsTransTemp = ConverterConstants.POINTS_TRANS_CONVERTER.toPointsTransTemp(pointsTrans);
        pointsTransTemp.setTransFlag(TransFlag.WAITING.getCode());
        return pointsTransTemp;
    }
}
