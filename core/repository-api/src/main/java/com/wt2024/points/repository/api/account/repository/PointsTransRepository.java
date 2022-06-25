package com.wt2024.points.repository.api.account.repository;

import com.wt2024.points.repository.api.account.domain.PointsAccountDetailsDTO;
import com.wt2024.points.repository.api.account.domain.PointsDetailsBalanceDTO;
import com.wt2024.points.repository.api.account.domain.PointsTransDTO;
import com.wt2024.points.repository.api.account.domain.PointsTransDetailsDTO;

import java.util.Date;
import java.util.List;

/**
 * @author Shade.Yang [shade.yang@aliyun.com]
 * @date 2021/12/16 16:56
 * @project points3.0:com.wt2024.points.repository.api.points.repository
 */
public interface PointsTransRepository {

    List<PointsTransDTO> queryAccountingTrans(String customerId, String pointsTypeNo);

    List<PointsAccountDetailsDTO> queryPointsAccountDetails(String customerId, String pointsTypeNo, List<String> accountingTransNo);

    List<PointsTransDTO> queryPointsTransByPage(String customerId, String institutionId, String pointsTypeNo, long fromId, Date fromDate, Date toDate, int index, int pageSize);

    PointsTransDTO existsPointsBySysTransNo(String sysTransNo);

    void processTrans(PointsTransDTO pointsTrans, PointsTransDTO expirePointsTrans);

    void expireProcessTrans(PointsTransDTO expirePointsTrans);

    List<PointsTransDTO> queryPointsBySysTransNoAndStatus(String sysTransNo, String status);

    boolean existsAccountingTrans(String customerId, String pointsTypeNo, String transNo, boolean include);

    List<PointsTransDetailsDTO> queryPointsTransDetailsByTransNo(String transNo);

    List<PointsTransDetailsDTO> queryPointsTransDetailsBySourceTransNo(String transNo);

    List<PointsTransDTO> queryAllWantToAsync(String customerId, String pointsTypeNo, Long startIndex, Integer pageSize);

    void processAsyncTrans(PointsTransDTO transDTO);

    List<PointsTransDTO> queryPointsByOldTransNoAndStatus(String transNo, String status);

    int deletePointsTempHasDone();

    List<PointsDetailsBalanceDTO> queryPointsDetailsBalanceByTransNo(String customerId, String pointsTypeNo, List<String> transNoList);

    List<PointsTransDetailsDTO> queryBackPointsTransDetailsByTransNo(List<String> transNoList);
}
