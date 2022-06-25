package com.wt2024.points.dubbo.backend.restful;

/**
 * @Author Shade.Yang [shade.yang@aliyun.com]
 * @Date 2019-01-08 21:40
 * @Project openapi:com.wt2024.points.restful
 */

import com.fasterxml.jackson.databind.ObjectMapper;
import com.wt2024.points.dubbo.backend.retcode.MsgRetCode;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.SneakyThrows;

/**
 * 服务返回给客户端的json对象包装
 */
@ApiModel(value = "响应返回实体")
public class JsonResult<T> {

    @ApiModelProperty(value = "通讯成功失败标志")
    private boolean flag = false;
    @ApiModelProperty(value = "通讯响应码，成功为0000")
    private String retcode = "";
    @ApiModelProperty(value = "通讯响应信息")
    private String retmsg = "";
    @ApiModelProperty(value = "通讯报文体")
    private T data = null;

    public JsonResult() {

    }

    public JsonResult(boolean status, MsgRetCode msgRetCode) {
        this.flag = status;
        this.retcode = msgRetCode.getRetCode();
        this.retmsg = msgRetCode.getRetShow();
    }

    @Deprecated
    public JsonResult(boolean status, String retcode, String retmsg) {
        this.flag = status;
        this.retcode = retcode;
        this.retmsg = retmsg;
    }

    public JsonResult(boolean status, String retcode, String retmsg, String... args) {
        this.flag = status;
        this.retcode = retcode;
        this.retmsg = String.format(retmsg, args);
    }

    public JsonResult(boolean status, MsgRetCode msgRetCode, T data) {
        this.flag = status;
        this.retcode = msgRetCode.getRetCode();
        this.retmsg = msgRetCode.getRetShow();
        this.data = data;
    }

    public boolean isFlag() {
        return flag;
    }

    public void setFlag(boolean flag) {
        this.flag = flag;
    }

    public String getRetcode() {
        return retcode;
    }

    public void setRetcode(String retcode) {
        this.retcode = retcode;
    }

    public String getRetmsg() {
        return retmsg;
    }

    public void setRetmsg(String retmsg) {
        this.retmsg = retmsg;
    }

    public T getData() {

        return data;
    }


    public void setData(T data) {

        this.data = data;
    }

    public void setMsgRetCode(MsgRetCode msgRetCode) {
        this.retcode = msgRetCode.getRetCode();
        this.retmsg = msgRetCode.getRetShow();
    }

    public void setMsgRetCode(MsgRetCode msgRetCode, String... string) {
        this.retcode = msgRetCode.getRetCode();
        this.retmsg = String.format(msgRetCode.getRetShow(), string);
    }

    @SneakyThrows
    @Override
    public String toString() {
        return new ObjectMapper().writeValueAsString(this);
    }
}
