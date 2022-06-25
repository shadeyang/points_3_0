package com.wt2024.points.restful.backend.controller;

import com.wt2024.points.core.api.domain.account.*;
import com.wt2024.points.core.api.service.AccountService;
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
 * @date 2021/12/30 16:04
 * @project points3.0:com.wt2024.points.restful.backend.controller
 */
@Api(tags = "积分账户")
@RestController
@RequestMapping(RestPathConstants.ACCOUNT.PATH)
@Validated
public class AccountController extends BaseController {

    @Resource
    private AccountService accountService;

    @ApiOperation("查询用户下所有积分账户")
    @PostMapping(RestPathConstants.ACCOUNT.QUERY_ALL_PATH)
    public ResponseResult<AccountInfoQueryAllOutput> queryAllAccountInfo(@RequestBody AccountInfoQueryAllInput accountInfoQueryAllInput) {
        return ResponseResult.builder().data(accountService.queryAllAccountInfo(accountInfoQueryAllInput)).build().success();
    }

    @ApiOperation("查询用户下指定积分类型的账户")
    @PostMapping(RestPathConstants.ACCOUNT.QUERY_PATH)
    public ResponseResult<AccountInfoQueryOutput> queryAccountInfoByPointsType(@RequestBody AccountInfoQueryInput accountInfoQueryInput) {
        return ResponseResult.builder().data(accountService.queryAccountInfoByPointsType(accountInfoQueryInput)).build().success();
    }

    @ApiOperation("查询用户下指定积分类型的账户明细")
    @PostMapping(RestPathConstants.ACCOUNT.QUERY_DETAIL_PATH)
    public ResponseResult<AccountDetailsQueryOutput> queryAccountDetails(@RequestBody AccountDetailsQueryInput accountDetailsQueryInput) {
        return ResponseResult.builder().data(accountService.queryAccountDetails(accountDetailsQueryInput)).build().success();
    }
}
