package com.wt2024.points.restful.backend.controller;

import com.wt2024.points.core.api.domain.customer.CustomerCreateInput;
import com.wt2024.points.core.api.domain.customer.CustomerCreateOutput;
import com.wt2024.points.core.api.domain.customer.CustomerInfoInput;
import com.wt2024.points.core.api.domain.customer.CustomerInfoOutput;
import com.wt2024.points.core.api.service.CustomerService;
import com.wt2024.points.restful.backend.constant.RestPathConstants;
import com.wt2024.points.restful.backend.domain.ResponseResult;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;

/**
 * @author Shade.Yang [shade.yang@aliyun.com]
 * @date 2022/1/25 15:32
 * @project points3.0:com.wt2024.points.restful.backend.controller
 */
@Api(tags = "积分客户")
@RestController
@RequestMapping(RestPathConstants.CUSTOMER.PATH)
@Validated
public class CustomerController extends BaseController {

    @Resource
    private CustomerService customerService;

    @ApiOperation("查询积分客户的信息")
    @PostMapping(RestPathConstants.CUSTOMER.QUERY_PATH)
    public ResponseResult<CustomerInfoOutput> queryCustomerInfo(@RequestBody CustomerInfoInput customerInfoInput) {
        return ResponseResult.builder().data(customerService.queryCustomerInfo(customerInfoInput)).build().success();
    }

    @ApiOperation("创建积分客户")
    @PostMapping(RestPathConstants.CUSTOMER.CREATE_PATH)
    public ResponseResult<CustomerCreateOutput> createCustomer(@RequestBody CustomerCreateInput customerCreateInput){
        return ResponseResult.builder().data(customerService.createCustomer(customerCreateInput)).build().success();
    }

}
