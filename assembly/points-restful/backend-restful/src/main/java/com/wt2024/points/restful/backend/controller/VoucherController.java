package com.wt2024.points.restful.backend.controller;

import com.wt2024.points.core.api.domain.voucher.AddVoucherInput;
import com.wt2024.points.core.api.service.VoucherService;
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
 * @date 2022/1/25 16:23
 * @project points3.0:com.wt2024.points.restful.backend.controller
 */
@Api(tags = "积分客户凭证")
@RestController
@RequestMapping(RestPathConstants.VOUCHER.PATH)
@Validated
public class VoucherController extends BaseController {

    @Resource
    private VoucherService voucherService;

    @ApiOperation("加挂客户凭证")
    @PostMapping(RestPathConstants.VOUCHER.ADD_PATH)
    public ResponseResult addVoucher(@RequestBody AddVoucherInput addVoucherInput) {
        voucherService.addVoucher(addVoucherInput);
        return ResponseResult.builder().build().success();
    }

}
