package com.wt2024.points.core.service;

import com.wt2024.points.common.code.PointsCode;
import com.wt2024.points.common.exception.PointsException;
import com.wt2024.points.core.api.domain.voucher.AddVoucherInput;
import com.wt2024.points.core.api.service.VoucherService;
import com.wt2024.points.core.converter.ConverterConstants;
import com.wt2024.points.repository.api.customer.domain.CustomerDTO;
import com.wt2024.points.repository.api.customer.repository.CustomerRepository;
import com.wt2024.points.repository.api.voucher.domain.VoucherDTO;
import com.wt2024.points.repository.api.voucher.repository.VoucherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.Objects;

/**
 * @author Shade.Yang [shade.yang@aliyun.com]
 * @date 2022/1/25 10:23
 * @project points3.0:com.wt2024.points.core.service
 */
@Service
@Validated
public class VoucherServiceImpl implements VoucherService {

    private VoucherRepository voucherRepository;

    private CustomerRepository customerRepository;

    @Override
    public void addVoucher(AddVoucherInput addVoucherInput) {
        CustomerDTO customerDTO = customerRepository.queryCustomerById(addVoucherInput.getCustomerId());
        if (Objects.isNull(customerDTO)) {
            throw new PointsException(PointsCode.TRANS_1001);
        }
        VoucherDTO voucherDTO = voucherRepository.queryVoucher(addVoucherInput.getVoucher().getVoucherType().getType(), addVoucherInput.getVoucher().getVoucherNo());
        if (Objects.nonNull(voucherDTO)) {
            throw new PointsException(PointsCode.TRANS_1002);
        }
        voucherRepository.addVoucher(ConverterConstants.VOUCHER_CONVERTER.toVoucherDTO(addVoucherInput));
    }

    @Autowired
    public void setVoucherRepository(VoucherRepository voucherRepository) {
        this.voucherRepository = voucherRepository;
    }
    @Autowired
    public void setCustomerRepository(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }
}
