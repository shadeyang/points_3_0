package com.wt2024.points.dubbo.backend.controller;


import com.wt2024.points.dubbo.backend.domain.Index;
import com.wt2024.points.dubbo.backend.restful.JsonResult;
import com.wt2024.points.dubbo.backend.retcode.MsgRetCode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.ws.rs.core.MediaType;

/**
 * @ClassName IndexController
 * @Description: TODO
 * @Author shade.yang
 * @Date 2020/4/16
 * @Version V1.0
 **/
@Controller
@RequestMapping("/index")
public class IndexController extends BaseController {

    @Value("${info.app.name}")
    private String appName;
    @Value("${info.app.version}")
    private String appVersion;

    @RequestMapping(value = "", produces = MediaType.APPLICATION_JSON, method = {RequestMethod.POST, RequestMethod.GET})
    @ResponseBody
    public JsonResult<Object> index() {
        Index indexDomain = new Index();
        indexDomain.setMsgRetCode(MsgRetCode.TRANS_0002, appName, appVersion);
        return new JsonResult<>(true, MsgRetCode.COMM_0000, indexDomain);
    }

}
