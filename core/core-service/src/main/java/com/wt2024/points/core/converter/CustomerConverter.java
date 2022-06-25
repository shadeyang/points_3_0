package com.wt2024.points.core.converter;

import com.wt2024.points.common.enums.Gender;
import com.wt2024.points.core.api.domain.customer.CustomerCreateInput;
import com.wt2024.points.core.api.domain.customer.CustomerInfoOutput;
import com.wt2024.points.core.api.domain.valid.CustomerInfoValidResult;
import com.wt2024.points.core.api.domain.voucher.Voucher;
import com.wt2024.points.repository.api.customer.domain.CustomerDTO;
import com.wt2024.points.repository.api.voucher.domain.VoucherDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * @ClassName CustomerConverter
 * @Description: TODO
 * @Author shade.yang
 * @Date 2022/1/1
 * @Version V1.0
 **/
@Mapper
public interface CustomerConverter {

    CustomerInfoValidResult.Customer toCustomer(CustomerDTO customerDTO);

    CustomerInfoOutput toCustomerInfoOutput(CustomerInfoValidResult.Customer customer);

    @Mapping(source = "gender.type", target = "gender")
    CustomerDTO toCustomerDTO(CustomerCreateInput customerCreateInput);

    @Mapping(source = "voucherType.type", target = "voucherTypeNo")
    VoucherDTO toVoucherDTO(Voucher voucher);

    default Gender toGender(String gender) {
        return Gender.getEnum(gender);
    }
}
