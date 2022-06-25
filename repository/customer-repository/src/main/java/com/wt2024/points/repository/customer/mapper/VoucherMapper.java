package com.wt2024.points.repository.customer.mapper;

import com.wt2024.points.repository.customer.entity.Voucher;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface VoucherMapper {
    int insert(Voucher record);

    int insertSelective(Voucher record);

    Voucher selectByVoucher(Voucher voucher);

    List<Voucher> selectByCustomerId(@Param("customerId")String customerId);

    Integer updateByVoucher(Voucher voucher);

    Integer deleteByVoucher(Voucher voucher);
}