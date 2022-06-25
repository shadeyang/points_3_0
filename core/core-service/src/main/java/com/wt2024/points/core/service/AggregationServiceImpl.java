package com.wt2024.points.core.service;

import com.wt2024.points.common.code.PointsCode;
import com.wt2024.points.common.enums.VoucherType;
import com.wt2024.points.common.exception.PointsException;
import com.wt2024.points.core.api.domain.valid.CustomerInfoValidResult;
import com.wt2024.points.core.api.service.AggregationService;
import com.wt2024.points.core.converter.ConverterConstants;
import com.wt2024.points.repository.api.customer.domain.CustomerDTO;
import com.wt2024.points.repository.api.customer.repository.CustomerRepository;
import com.wt2024.points.repository.api.system.domain.InstitutionDTO;
import com.wt2024.points.repository.api.system.repository.InstitutionRepository;
import com.wt2024.points.repository.api.voucher.domain.VoucherDTO;
import com.wt2024.points.repository.api.voucher.repository.VoucherRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Objects;

/**
 * @author Shade.Yang [shade.yang@aliyun.com]
 * @date 2021/12/30 14:53
 * @project points3.0:com.wt2024.points.core.service
 */
@Service
@Validated
public class AggregationServiceImpl implements AggregationService {

    private CustomerRepository customerRepository;

    private VoucherRepository voucherRepository;

    private InstitutionRepository institutionRepository;

    @Override
    public CustomerInfoValidResult checkInputCustomerInfo(@NotNull VoucherType voucherType, @NotEmpty String voucherNo, @NotEmpty String institutionNo) {
        InstitutionDTO institution = institutionRepository.queryTopInstitution(institutionNo);
        if (Objects.isNull(institution)) {
            throw new PointsException(PointsCode.TRANS_1101);
        }

        String customerId = voucherNo;
        if (!VoucherType.CUST.equals(voucherType)) {
            VoucherDTO voucher = voucherRepository.queryVoucher(voucherType.getType(), voucherNo);
            if (Objects.isNull(voucher)) {
                throw new PointsException(PointsCode.TRANS_1003);
            }
            customerId = voucher.getCustomerId();
        }

        CustomerDTO customer = customerRepository.queryCustomerById(customerId);
        if (customer == null) {
            throw new PointsException(PointsCode.TRANS_1001);
        }

        return CustomerInfoValidResult.builder()
                .customer(ConverterConstants.CUSTOMER_CONVERTER.toCustomer(customer))
                .institution(ConverterConstants.INSTITUTION_CONVERTER.toInstitution(institution))
                .build();
    }

    @Autowired
    public void setCustomerRepository(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }
    @Autowired
    public void setVoucherRepository(VoucherRepository voucherRepository) {
        this.voucherRepository = voucherRepository;
    }
    @Autowired
    public void setInstitutionRepository(InstitutionRepository institutionRepository) {
        this.institutionRepository = institutionRepository;
    }
}
