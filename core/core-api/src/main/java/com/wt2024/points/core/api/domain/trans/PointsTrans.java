package com.wt2024.points.core.api.domain.trans;

import com.wt2024.points.common.enums.DebitOrCredit;
import com.wt2024.points.common.enums.ReversedFlag;
import com.wt2024.points.core.api.domain.voucher.Voucher;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * @ClassName PointsTransDomain
 * @Description: TODO
 * @Author shade.yang
 * @Date 2021/6/2
 * @Version V1.0
 **/
@Getter
@Setter
public class PointsTrans implements Serializable {

    private Long id;

    private String transNo;

    private Trans trans;

    private Points points;

    private String institutionId;

    private DebitOrCredit debitOrCredit;

    private ReversedFlag reversedFlag;

    private String merchantNo;

    private Voucher voucher;

    private String operator;

    private String description;
}
