package com.wt2024.points.restful.backend.controller.example;

import com.wt2024.points.common.code.PointsCode;
import com.wt2024.points.common.exception.PointsException;
import com.wt2024.points.restful.backend.controller.BaseController;
import com.wt2024.points.restful.backend.controller.example.domain.TestData;
import com.wt2024.points.restful.backend.controller.example.domain.TestDataValid;
import com.wt2024.points.restful.backend.domain.Index;
import com.wt2024.points.restful.backend.domain.ResponseResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Shade.Yang [shade.yang@aliyun.com]
 * @date 2022/1/1 14:48
 * @project points3.0:com.wt2024.points.restful.backend.controller.example
 */
@RestController
@RequestMapping("/test")
@Validated
public class ExampleController extends BaseController {

    @PostMapping("/index")
    public ResponseResult<Index> index() {
        return ResponseResult.builder().build().success();
    }

    @PostMapping("/data/index")
    public ResponseResult<Index> index(TestData testData) {
        return ResponseResult.builder().build().success();
    }

    @PostMapping("/data/valid")
    public ResponseResult<Index> index(@Validated TestDataValid testDataValid) {
        return ResponseResult.builder().build().success();
    }

    @PostMapping("/points/exception")
    public ResponseResult<Index> pointsException() throws PointsException {
        if (1 > 0) {
            throw new PointsException(PointsCode.TRANS_1003);
        }
        return ResponseResult.builder().build().success();
    }

    @PostMapping("/exception")
    public ResponseResult<Index> exception() {
        if (1 > 0) {
            throw new RuntimeException("模拟测试异常");
        }
        return ResponseResult.builder().build().success();
    }
}
