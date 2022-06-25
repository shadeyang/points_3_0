package com.wt2024.points.core.api.domain.account;

import com.wt2024.points.core.api.domain.OutputBase;
import com.wt2024.points.core.api.domain.trans.PointsTrans;
import lombok.*;

import java.util.List;

/**
 * @ClassName AccountDetailQueryOutputDomain
 * @Description: TODO
 * @Author shade.yang
 * @Date 2021/5/26
 * @Version V1.0
 **/
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AccountDetailsQueryOutput extends OutputBase {

    private List<PointsAccountDetails> pointsAccountDetailsList;

    private List<PointsTrans> pointsTransList;

}
