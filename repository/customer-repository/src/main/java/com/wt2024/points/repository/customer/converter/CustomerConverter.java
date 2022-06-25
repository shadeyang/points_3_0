package com.wt2024.points.repository.customer.converter;

import com.wt2024.points.repository.api.customer.domain.CustomerDTO;
import com.wt2024.points.repository.customer.entity.Customer;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * @ClassName CustomerConverter
 * @Description: TODO
 * @Author shade.yang
 * @Date 2022/1/2
 * @Version V1.0
 **/
@Mapper
public interface CustomerConverter {

    @Mapping(source = "phone",target = "phoneNumber")
    CustomerDTO toCustomerDTO (Customer customer);

    @Mapping(source = "phoneNumber",target = "phone")
    Customer toCustomer(CustomerDTO customerDTO);
}
