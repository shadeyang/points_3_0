package com.wt2024.points.restful.backend.controller;


import com.wt2024.points.common.code.PointsCode;
import com.wt2024.points.restful.backend.domain.Index;
import com.wt2024.points.restful.backend.domain.ResponseResult;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import javax.ws.rs.core.MediaType;

/**
 * @ClassName IndexController
 * @Description: TODO
 * @Author shade.yang
 * @Date 2020/4/16
 * @Version V1.0
 **/
@RestController
@RequestMapping("/index")
public class IndexController extends BaseController {

    @Value("${info.app.name}")
    private String appName;
    @Value("${info.app.version}")
    private String appVersion;

    @RequestMapping(produces = MediaType.APPLICATION_JSON,method = {RequestMethod.POST, RequestMethod.GET})
    public ResponseResult<Index> index() {
        return ResponseResult.builder().build().fail(PointsCode.TRANS_0002, appName, appVersion);
    }

}
