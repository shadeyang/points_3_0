package com.wt2024.points.core.handler.trans.vo;

import com.wt2024.points.core.api.domain.trans.PointsAccTransInput;
import com.wt2024.points.core.api.domain.trans.PointsAccTransOutput;
import com.wt2024.points.core.api.domain.valid.CustomerInfoValidResult;
import com.wt2024.points.repository.api.account.domain.PointsTransDTO;
import com.wt2024.points.repository.api.account.domain.PointsTypeDTO;
import lombok.Getter;
import lombok.Setter;

import java.util.Date;

/**
 * @ClassName AccountTransVo
 * @Description: TODO
 * @Author shade.yang
 * @Date 2021/8/25
 * @Version V1.0
 **/

@Getter
@Setter
public class AccountTransVo extends AbstractTransVo<PointsAccTransInput, PointsAccTransOutput> {
    private CustomerInfoValidResult.Customer customer;
    private CustomerInfoValidResult.Institution institution;
    private PointsTransDTO pointsTrans;
    private PointsTypeDTO pointsType;
    private Date endDate;

    public AccountTransVo(PointsAccTransInput input) {
        super(input);
        this.output = PointsAccTransOutput.builder().build();
        this.customer = input.getCustomerInfo().getCustomer();
        this.institution = input.getCustomerInfo().getInstitution();
    }
}
