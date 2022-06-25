package com.wt2024.points.restful.backend.controller;

import com.wt2024.points.core.api.domain.trans.*;
import com.wt2024.points.core.api.service.TransactionService;
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
 * @date 2022/1/26 10:34
 * @project points3.0:com.wt2024.points.restful.backend.controller
 */
@Api(tags = "积分交易")
@RestController
@RequestMapping(RestPathConstants.TRANS.PATH)
@Validated
public class TransactionController extends BaseController{

    @Resource
    private TransactionService transactionService;

    @ApiOperation("查询交易流水")
    @PostMapping(RestPathConstants.TRANS.QUERY_PATH)
    public ResponseResult<PointsTransQueryListOutput> queryPointsTransList(@RequestBody PointsTransQueryListInput pointsTransQueryListInput) {
        return ResponseResult.builder().data(transactionService.queryPointsTransList(pointsTransQueryListInput)).build().success();
    }

    @ApiOperation("积分消费交易")
    @PostMapping(RestPathConstants.TRANS.CONSUME_PATH)
    public ResponseResult<PointsConsumeOutput> consumePoints(@RequestBody PointsConsumeInput pointsConsumeInput) {
        return ResponseResult.builder().data(transactionService.consumePoints(pointsConsumeInput)).build().success();
    }

    @ApiOperation(value = "积分账务交易",notes = "账务借贷操作")
    @PostMapping(RestPathConstants.TRANS.ACC_TRANS_PATH)
    public ResponseResult<PointsAccTransOutput> accTransPoints(@RequestBody PointsAccTransInput pointsAccTransInput) {
        return ResponseResult.builder().data(transactionService.accTransPoints(pointsAccTransInput)).build().success();
    }

    @ApiOperation("查询交易状态")
    @PostMapping(RestPathConstants.TRANS.QUERY_STATE_PATH)
    public ResponseResult<PointsQueryStateOutput> queryState(@RequestBody PointsQueryStateInput pointsQueryStateInput) {
        return ResponseResult.builder().data(transactionService.queryState(pointsQueryStateInput)).build().success();
    }

    @ApiOperation("积分交易冲正")
    @PostMapping(RestPathConstants.TRANS.REVERSE_PATH)
    public ResponseResult<PointsReverseOutput> reversePoints(@RequestBody PointsReverseInput pointsReverseInput){
        return ResponseResult.builder().data(transactionService.reversePoints(pointsReverseInput)).build().success();
    }

    @ApiOperation("积分消费退货")
    @PostMapping(RestPathConstants.TRANS.BACK_PATH)
    public ResponseResult<PointsBackOutput> backPoints(@RequestBody PointsBackInput pointsBackInput){
        return ResponseResult.builder().data(transactionService.backPoints(pointsBackInput)).build().success();
    }
}
