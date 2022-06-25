package com.wt2024.points.repository.account.mapper;

import com.wt2024.points.repository.account.entity.PointsTransTemp;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface PointsTransTempMapper {

    int insert(PointsTransTemp record);

    /**
     * 分页查询待处理交易流水
     **/
    List<PointsTransTemp> queryAllWantToAsync(@Param("customerId") String customerId, @Param("pointsTypeNo") String pointsTypeNo, @Param("startIndex") Long startIndex, @Param("pageSize") Integer pageSize);

    /**
     * 删除已处理流水
     **/
    int deleteHasDone();

    /**
     * 根据id查询待处理流水
     **/
    PointsTransTemp queryById(@Param("id") Long id, @Param("transFlag") String transFlag);

    /**
     * 根据TransNo查询待处理流水
     **/
    PointsTransTemp queryByTransNo(@Param("transNo") String transNo);

    /**
     * 根据id将待处理流水转为进行中
     **/
    int processUnDoPointsTrans(@Param("id") Long id);

    /**
     * 根据id将处理中流水转为完成
     *
     * @param id
     * @return
     */
    int processDonePointsTrans(@Param("id") Long id);

    /**
     * 查找执行客户的代记账流水
     *
     * @param customerId
     * @param pointsTypeNo
     * @return
     */
    List<PointsTransTemp> queryCustomerAccountingPointsTrans(@Param("customerId") String customerId, @Param("pointsTypeNo") String pointsTypeNo);

    /**
     * 查找一个当前流水号后是否存在待记账的流水
     *
     * @param customerId
     * @param pointsTypeNo
     * @param startTransNo
     * @return
     */
    PointsTransTemp selectOneCustomerAccountingPointsTrans(@Param("customerId") String customerId, @Param("pointsTypeNo") String pointsTypeNo, @Param("startTransNo") String startTransNo, @Param("include") boolean include);

}