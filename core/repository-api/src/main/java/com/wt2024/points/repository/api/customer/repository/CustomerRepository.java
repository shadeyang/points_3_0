package com.wt2024.points.repository.api.customer.repository;

import com.wt2024.points.repository.api.customer.domain.CustomerDTO;
import com.wt2024.points.repository.api.voucher.domain.VoucherDTO;

import java.util.List;

/**
 * @ClassName CustomersRepository
 * @Description: TODO
 * @Author shade.yang
 * @Date 2022/1/1
 * @Version V1.0
 **/
public interface CustomerRepository {
    CustomerDTO queryCustomerById(String customerId);

    CustomerDTO createCustomer(CustomerDTO customerDTO, VoucherDTO voucherDTO);

    List<CustomerDTO> queryCustomerList(CustomerDTO customerDTO);

    Integer updateCustomer(CustomerDTO customerDTO);

    Integer deleteCustomerByIds(String[] customerIds);
}
