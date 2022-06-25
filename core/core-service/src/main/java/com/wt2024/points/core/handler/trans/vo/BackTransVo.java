package com.wt2024.points.core.handler.trans.vo;

import com.wt2024.points.core.api.domain.trans.PointsBackInput;
import com.wt2024.points.core.api.domain.trans.PointsBackOutput;
import com.wt2024.points.repository.api.account.domain.PointsTransDTO;
import com.wt2024.points.repository.api.account.domain.PointsTransDetailsDTO;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Shade.Yang [shade.yang@aliyun.com]
 * @date 2022/5/18 16:43
 * @project points3.0:com.wt2024.points.core.handler.trans.vo
 */
@Getter
@Setter
public class BackTransVo extends AbstractTransVo<PointsBackInput, PointsBackOutput>{

    private PointsTransDTO oldPointsTrans;

    private PointsTransDTO pointsTrans;

    private PointsTransDTO expirePointsTrans;

    private boolean isSameDay = false;

    private LocalDateTime backDateTime;

    private List<PointsTransDTO> backPointsTransList;

    private List<PointsTransDetailsDTO> oldPointsTransDetailsList;

    private BigDecimal remainPointAmount;

    private BigDecimal remainClearingAmt;

    public BackTransVo(PointsBackInput input) {
        super(input);
        this.backDateTime = LocalDateTime.now();
        this.output = PointsBackOutput.builder().build();
        this.backPointsTransList = new ArrayList<>();
        this.oldPointsTransDetailsList = new ArrayList<>();
        this.remainPointAmount = BigDecimal.ZERO;
        this.remainClearingAmt = BigDecimal.ZERO;
    }
}
