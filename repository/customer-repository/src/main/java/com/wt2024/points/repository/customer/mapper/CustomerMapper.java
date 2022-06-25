package com.wt2024.points.repository.customer.mapper;

import com.wt2024.points.repository.customer.entity.Customer;
import org.apache.ibatis.annotations.Param;

import java.util.List;

public interface CustomerMapper {
    int deleteByPrimaryKey(String customerId);

    int insert(Customer record);

    int insertSelective(Customer record);

    Customer selectByPrimaryKey(String customerId);

    int updateByPrimaryKeySelective(Customer record);

    int updateByPrimaryKey(Customer record);

    List<Customer> selectByCustomerName(@Param("customerName") String customerName);

    List<Customer> selectByCustomer(Customer customer);
}