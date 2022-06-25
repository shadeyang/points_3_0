package com.wt2024.points.dubbo.backend.exception;

import com.wt2024.points.dubbo.backend.retcode.MsgRetCode;

/**
 * @ClassName PointsException
 * @Description: TODO
 * @Author shade.yang
 * @Date 2021/8/10
 * @Version V1.0
 **/
public class PointsTransException extends RuntimeException {

    private MsgRetCode retCode;

    public PointsTransException(MsgRetCode retCode) {
        super(retCode.toString());
        this.retCode = retCode;
    }

    public PointsTransException(MsgRetCode retCode, Throwable cause) {
        super(retCode.toString(), cause);
        this.retCode = retCode;
    }

    public MsgRetCode getRetCode() {
        return retCode;
    }

}
