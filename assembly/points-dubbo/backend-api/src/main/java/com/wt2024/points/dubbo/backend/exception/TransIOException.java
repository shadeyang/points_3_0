package com.wt2024.points.dubbo.backend.exception;

import com.wt2024.points.dubbo.backend.retcode.MsgRetCode;

import java.io.IOException;

/**
 * @ClassName TransIOException
 * @Description: TODO
 * @Author shade.yang
 * @Date 2021/4/7
 * @Version V1.0
 **/
public class TransIOException extends IOException {

    private MsgRetCode msgRetCode;

    public TransIOException(MsgRetCode msgRetCode) {
        super(msgRetCode.getRetShow());
        this.msgRetCode = msgRetCode;
    }

    public TransIOException(MsgRetCode msgRetCode, String... string) {
        super(String.format(msgRetCode.getRetShow(), string));
        this.msgRetCode = msgRetCode;
        this.msgRetCode.setRetCode(msgRetCode.getRetCode());
        this.msgRetCode.setRetShow(String.format(msgRetCode.getRetShow(), string));
    }

    public TransIOException(String retCode, String retShow) {
        super(retShow);
        this.msgRetCode = MsgRetCode.COMM_0011;
        this.msgRetCode.setRetCode(retCode);
        this.msgRetCode.setRetShow(retShow);
    }

    public MsgRetCode getMsgRetCode() {
        return msgRetCode;
    }

    public void setMsgRetCode(MsgRetCode msgRetCode) {
        this.msgRetCode = msgRetCode;
    }
}
