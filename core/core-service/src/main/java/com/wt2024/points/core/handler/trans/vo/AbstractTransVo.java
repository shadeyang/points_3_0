package com.wt2024.points.core.handler.trans.vo;

import com.wt2024.points.common.code.PointsCode;
import com.wt2024.points.core.api.domain.InputBase;
import com.wt2024.points.core.api.domain.OutputBase;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName AbstractTransVo
 * @Description: TODO
 * @Author shade.yang
 * @Date 2021/8/25
 * @Version V1.0
 **/
@Getter
@Setter
public abstract class AbstractTransVo<S extends InputBase, T extends OutputBase> {
    protected T output;
    protected S input;
    protected List<String> lockSuccessKeys = new ArrayList<>();
    protected boolean failCalled = false;
    protected String code;
    protected String message;

    public AbstractTransVo(S input) {
        this.input = input;
        this.setRetCode(PointsCode.TRANS_0005);
    }

    public void setRetCode(PointsCode pointsCode) {
        this.code = pointsCode.getCode();
        this.message = pointsCode.getShow();
    }

    public void setRetCode(PointsCode pointsCode, String... string) {
        this.code = pointsCode.getCode();
        this.message = String.format(pointsCode.getShow(), string);
    }

    public boolean success(){
        return PointsCode.TRANS_0000.getCode().equals(this.code);
    }
}
