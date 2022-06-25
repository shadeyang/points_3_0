package com.wt2024.points.repository.customer;

import com.wt2024.points.common.sequence.Sequence;
import com.wt2024.points.repository.api.customer.domain.CustomerDTO;
import com.wt2024.points.repository.api.customer.repository.CustomerRepository;
import com.wt2024.points.repository.api.voucher.domain.VoucherDTO;
import com.wt2024.points.repository.customer.converter.ConverterConstants;
import com.wt2024.points.repository.customer.entity.Customer;
import com.wt2024.points.repository.customer.entity.Voucher;
import com.wt2024.points.repository.customer.mapper.CustomerMapper;
import com.wt2024.points.repository.customer.mapper.VoucherMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

/**
 * @ClassName CustomerRepositoryImpl
 * @Description: TODO
 * @Author shade.yang
 * @Date 2022/1/2
 * @Version V1.0
 **/
@Slf4j
@Repository
public class CustomerRepositoryImpl implements CustomerRepository {
    @Autowired
    CustomerMapper customerMapper;
    @Autowired
    VoucherMapper voucherMapper;

    @Override
    public CustomerDTO queryCustomerById(String customerId) {
        return ConverterConstants.CUSTOMER_CONVERTER.toCustomerDTO(customerMapper.selectByPrimaryKey(customerId));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public CustomerDTO createCustomer(CustomerDTO customerDTO, VoucherDTO voucherDTO) {
        String customerId = Sequence.getCustomersId();
        Customer customer = ConverterConstants.CUSTOMER_CONVERTER.toCustomer(customerDTO);
        customer.setCustomerId(customerId);
        customerMapper.insert(customer);
        if(!Objects.isNull(voucherDTO)) {
            Voucher voucher = ConverterConstants.VOUCHER_CONVERTER.toVoucher(voucherDTO);
            voucher.setCustomerId(customerId);
            voucherMapper.insert(voucher);
        }
        customerDTO.setCustomerId(customerId);
        return customerDTO;
    }

    @Override
    public List<CustomerDTO> queryCustomerList(CustomerDTO customerDTO) {
         Customer customer = ConverterConstants.CUSTOMER_CONVERTER.toCustomer(customerDTO);
        return customerMapper.selectByCustomer(customer).stream().map(ConverterConstants.CUSTOMER_CONVERTER::toCustomerDTO).collect(Collectors.toList());
    }

    @Override
    public Integer updateCustomer(CustomerDTO customerDTO) {
        Customer customer = ConverterConstants.CUSTOMER_CONVERTER.toCustomer(customerDTO);
        return customerMapper.updateByPrimaryKey(customer);
    }

    @Override
    public Integer deleteCustomerByIds(String[] customerIds) {
        AtomicReference<Integer> result = new AtomicReference<>(0);
        Arrays.stream(customerIds).forEach(customerId -> result.updateAndGet(v -> v + customerMapper.deleteByPrimaryKey(customerId)));
        return result.get();
    }
}
