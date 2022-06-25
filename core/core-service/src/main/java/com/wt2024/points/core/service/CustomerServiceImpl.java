package com.wt2024.points.core.service;

import com.wt2024.points.common.code.PointsCode;
import com.wt2024.points.common.exception.PointsException;
import com.wt2024.points.core.api.domain.customer.CustomerCreateInput;
import com.wt2024.points.core.api.domain.customer.CustomerCreateOutput;
import com.wt2024.points.core.api.domain.customer.CustomerInfoInput;
import com.wt2024.points.core.api.domain.customer.CustomerInfoOutput;
import com.wt2024.points.core.api.domain.valid.CustomerInfoValidResult;
import com.wt2024.points.core.api.domain.voucher.Voucher;
import com.wt2024.points.core.api.service.CustomerService;
import com.wt2024.points.core.converter.ConverterConstants;
import com.wt2024.points.repository.api.account.domain.PointsAccountInfoDTO;
import com.wt2024.points.repository.api.account.repository.PointsAccountInfoRepository;
import com.wt2024.points.repository.api.customer.domain.CustomerDTO;
import com.wt2024.points.repository.api.customer.repository.CustomerRepository;
import com.wt2024.points.repository.api.system.domain.InstitutionDTO;
import com.wt2024.points.repository.api.system.repository.InstitutionRepository;
import com.wt2024.points.repository.api.voucher.domain.VoucherDTO;
import com.wt2024.points.repository.api.voucher.repository.VoucherRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @ClassName CustomerServiceImpl
 * @Description: TODO
 * @Author shade.yang
 * @Date 2022/1/3
 * @Version V1.0
 **/
@Service
@Validated
@Slf4j
public class CustomerServiceImpl implements CustomerService {

    public static final String INTERFACE = "interface";
    public static final String CUSTOMER_LVL = "0";

    private PointsAccountInfoRepository pointsAccountInfoRepository;

    private InstitutionRepository institutionRepository;

    private VoucherRepository voucherRepository;

    private CustomerRepository customerRepository;


    @Override
    public CustomerInfoOutput queryCustomerInfo(CustomerInfoInput customerInfoInput) {
        CustomerInfoValidResult customerInfo = customerInfoInput.getCustomerInfo();
        List<PointsAccountInfoDTO> pointsAccountInfoList = pointsAccountInfoRepository.queryPointsAccountInfo(customerInfo.getCustomerId(), customerInfoInput.getPointsTypeNo(), customerInfo.getInstitutionId());
        CustomerInfoOutput customerInfoOutput = ConverterConstants.CUSTOMER_CONVERTER.toCustomerInfoOutput(customerInfo.getCustomer());
        customerInfoOutput.setPointsAccountInfoList(pointsAccountInfoList.stream().map(ConverterConstants.POINTS_ACCOUNT_INFO_CONVERTER::toPointsAccountInfo).collect(Collectors.toList()));
        return customerInfoOutput;
    }

    @Override
    public CustomerCreateOutput createCustomer(CustomerCreateInput customerCreateInput) {

        InstitutionDTO institution = institutionRepository.queryTopInstitution(customerCreateInput.getInstitutionNo());
        if (Objects.isNull(institution)) {
            throw new PointsException(PointsCode.TRANS_1101);
        }
        Voucher voucherDomain = customerCreateInput.getVoucher();
        VoucherDTO voucher;
        if (Objects.nonNull(voucherDomain)) {
            voucher = voucherRepository.queryVoucher(voucherDomain.getVoucherType().getType(), voucherDomain.getVoucherNo());
            if (Objects.nonNull(voucher)) {
                log.info("当前传入凭证{},{}已存在，直接返回", voucherDomain.getVoucherType(), voucherDomain.getVoucherNo());
                return CustomerCreateOutput.builder().customerId(voucher.getCustomerId()).build();
            }
        }
        log.debug("进行开户");
        CustomerDTO customer = ConverterConstants.CUSTOMER_CONVERTER.toCustomerDTO(customerCreateInput);
        customer.setOperator(INTERFACE);
        customer.setCustomerLvl(CUSTOMER_LVL);
        customer.setInstitutionId(institution.getInstitutionId());

        voucher = ConverterConstants.CUSTOMER_CONVERTER.toVoucherDTO(voucherDomain);
        customer = customerRepository.createCustomer(customer, voucher);
        return CustomerCreateOutput.builder().customerId(customer.getCustomerId()).build();
    }

    @Autowired
    public void setPointsAccountInfoRepository(PointsAccountInfoRepository pointsAccountInfoRepository) {
        this.pointsAccountInfoRepository = pointsAccountInfoRepository;
    }

    @Autowired
    public void setInstitutionRepository(InstitutionRepository institutionRepository) {
        this.institutionRepository = institutionRepository;
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
