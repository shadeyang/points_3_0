package com.wt2024.points.core.handler.trans.vo;

import com.wt2024.points.core.api.domain.trans.PointsReverseInput;
import com.wt2024.points.core.api.domain.trans.PointsReverseOutput;
import com.wt2024.points.repository.api.account.domain.PointsTransDTO;
import com.wt2024.points.repository.api.account.domain.PointsTransDetailsDTO;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * @ClassName ReverseTransVo
 * @Description: TODO
 * @Author shade.yang
 * @Date 2021/8/26
 * @Version V1.0
 **/
@Getter
@Setter
public class ReverseTransVo extends AbstractTransVo<PointsReverseInput, PointsReverseOutput> {

    private PointsTransDTO oldPointsTrans;

    private PointsTransDTO pointsTrans;

    private PointsTransDTO expirePointsTrans;

    private boolean isSameDay = false;

    private LocalDateTime reverseDateTime;

    private List<PointsTransDetailsDTO> oldPointsTransDetailsList;

    public ReverseTransVo(PointsReverseInput input) {
        super(input);
        this.reverseDateTime = LocalDateTime.now();
        this.output = PointsReverseOutput.builder().build();
        this.oldPointsTransDetailsList = new ArrayList<>();
    }
}
