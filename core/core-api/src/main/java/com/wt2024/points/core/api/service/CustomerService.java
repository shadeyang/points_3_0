package com.wt2024.points.core.api.service;

import com.wt2024.points.core.api.domain.customer.CustomerCreateInput;
import com.wt2024.points.core.api.domain.customer.CustomerCreateOutput;
import com.wt2024.points.core.api.domain.customer.CustomerInfoInput;
import com.wt2024.points.core.api.domain.customer.CustomerInfoOutput;

import javax.validation.Valid;

/**
 * @ClassName CustomerService
 * @Description: TODO
 * @Author shade.yang
 * @Date 2022/1/3
 * @Version V1.0
 **/
public interface CustomerService {

    CustomerInfoOutput queryCustomerInfo(@Valid CustomerInfoInput customerInfoInput);

    CustomerCreateOutput createCustomer(@Valid CustomerCreateInput customerCreateInput);
}
