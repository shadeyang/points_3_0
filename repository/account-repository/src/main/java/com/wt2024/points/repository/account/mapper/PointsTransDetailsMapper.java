package com.wt2024.points.repository.account.mapper;

import com.wt2024.points.repository.account.entity.PointsTransDetails;
import com.wt2024.points.repository.account.vo.PointsAccountDetails;
import com.wt2024.points.repository.account.vo.PointsDetailsBalance;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface PointsTransDetailsMapper {

    int insert(PointsTransDetails record);

    List<PointsTransDetails> queryPointsTransDetailsBySourceTransNo(String transNo);

    int insertBatch(@Param("list") List<PointsTransDetails> pointsTransDetailsList);

    List<PointsTransDetails> queryPointsTransDetailsByCustomerAndPointType(@Param("customerId") String customerId, @Param("pointsTypeNo") String pointsTypeNo);

    List<PointsTransDetails> selectByTransNo(@Param("transNo") String transNo);

    List<PointsAccountDetails> queryPointsAccountDetail(@Param("customerId") String customerId, @Param("pointsTypeNo") String pointsTypeNo, @Param("excludeTrans") List<String> accountingTransNo);

    List<PointsTransDetails> selectBySourceTransNo(@Param("transNo") String sourceTransNo);


    /**
     * 根据贷入流水查询待使用积分情况
     *
     * @param transNoList
     * @return
     */
    List<PointsDetailsBalance> selectPointsDetailsBalanceByTransNo(@Param("customerId") String customerId, @Param("pointsTypeNo") String pointsTypeNo, @Param("transNos") List<String> transNoList);

    List<PointsTransDetails> selectBackPointsTransDetailsByTransNo(@Param("transNos")List<String> transNoList);
}