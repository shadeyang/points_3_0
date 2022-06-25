package com.wt2024.points.dubbo.backend.domain;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonPropertyOrder;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wt2024.points.dubbo.backend.retcode.MsgRetCode;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import lombok.SneakyThrows;

import java.io.Serializable;

/**
 * @Author Shade.Yang [shade.yang@aliyun.com]
 * @Date 2018/9/5 16:39
 * @Project rightsplat:com.uxunchina.rightsplat.dto
 */
@ApiModel(value = "基础响应类")
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonPropertyOrder(alphabetic = true)
@Getter
@Setter
public class ResponseBase implements Serializable {

    @ApiModelProperty(value = "业务结果响应码", required = true)
    private String retcode;
    @ApiModelProperty(value = "业务结果响应信息", required = true)
    private String retmsg;

    public ResponseBase() {

    }

    public ResponseBase(MsgRetCode msgRetCode) {
        this.retcode = msgRetCode.getRetCode();
        this.retmsg = msgRetCode.getRetShow();
    }

    public ResponseBase(String retcode, String retmsg) {
        this.retcode = retcode;
        this.retmsg = retmsg;
    }

    public ResponseBase(MsgRetCode msgRetCode, String... string) {
        this.retcode = msgRetCode.getRetCode();
        this.retmsg = String.format(msgRetCode.getRetShow(), string);
    }

    @SneakyThrows
    @Override
    public String toString() {
        return new ObjectMapper().writeValueAsString(this);
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

    public void setMsgRetCode(MsgRetCode msgRetCode) {
        this.retcode = msgRetCode.getRetCode();
        this.retmsg = msgRetCode.getRetShow();
    }

    public void setMsgRetCode(MsgRetCode msgRetCode, String... string) {
        this.retcode = msgRetCode.getRetCode();
        this.retmsg = String.format(msgRetCode.getRetShow(), string);
    }

    public boolean convert2Fail(ResponseBase target) {
        if (MsgRetCode.TRANS_0000.getRetCode().equals(this.retcode)) {
            return false;
        } else {
            target.setRetcode(this.retcode);
            target.setRetmsg(this.retmsg);
            return true;
        }
    }
}
