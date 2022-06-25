package com.wt2024.points.common.exception;

import com.wt2024.points.common.code.PointsCode;

/**
 * @author Shade.Yang [shade.yang@aliyun.com]
 * @date 2021/12/15 17:05
 * @project points3.0:com.wt2024.points.common.exception
 */
public class PointsException extends RuntimeException {

    private PointsCode pointsCode;

    public PointsException(PointsCode pointsCode) {
        super(pointsCode.getShow());
        this.pointsCode = pointsCode;
    }

    public PointsException(PointsCode pointsCode, String... string) {
        super(String.format(pointsCode.getShow(), string));
        this.pointsCode = pointsCode;
        this.pointsCode.setCode(pointsCode.getCode());
        this.pointsCode.setShow(String.format(pointsCode.getShow(), string));
    }

    public PointsException(String code, String show) {
        super(show);
        this.pointsCode = PointsCode.TRANS_0005;
        this.pointsCode.setCode(code);
        this.pointsCode.setShow(show);
    }

    public PointsCode getPointsCode() {
        return pointsCode;
    }
}
