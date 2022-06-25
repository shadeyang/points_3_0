package com.wt2024.points.core.handler.trans.vo;

import com.wt2024.points.core.api.domain.account.PointsAccountExpireInput;
import com.wt2024.points.core.api.domain.account.PointsAccountExpireOutput;
import com.wt2024.points.repository.api.account.domain.PointsDetailsBalanceDTO;
import com.wt2024.points.repository.api.account.domain.PointsTransDTO;
import lombok.Getter;
import lombok.Setter;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Shade.Yang [shade.yang@aliyun.com]
 * @date 2022/2/16 14:30
 * @project points3.0:com.wt2024.points.core.handler.trans.vo
 */
@Getter
@Setter
public class ExpireTransVo extends AbstractTransVo<PointsAccountExpireInput, PointsAccountExpireOutput>{

    private String customerId;
    private String pointsTypeNo;
    private List<String> TransNoList;
    private List<PointsDetailsBalanceDTO> pointsExpireInfo;
    List<PointsDetailsBalanceDTO> pointsExpireDelete;
    private List<PointsTransDTO> expirePointsTransList;
    private String institutionId;

    public ExpireTransVo(PointsAccountExpireInput input) {
        super(input);
        this.output = PointsAccountExpireOutput.builder().build();
        this.customerId = input.getCustomerId();
        this.pointsTypeNo = input.getPointsTypeNo();
        this.pointsExpireInfo = new ArrayList<>();
        this.expirePointsTransList = new ArrayList<>();
    }

}
