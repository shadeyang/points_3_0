package com.wt2024.points.core.handler.trans.vo;

import com.wt2024.points.core.api.domain.trans.PointsConsumeInput;
import com.wt2024.points.core.api.domain.trans.PointsConsumeOutput;
import com.wt2024.points.core.api.domain.valid.CustomerInfoValidResult;
import com.wt2024.points.repository.api.account.domain.PointsTransDTO;
import com.wt2024.points.repository.api.account.domain.PointsTypeDTO;
import lombok.Getter;
import lombok.Setter;

/**
 * @ClassName ConsumeTransVo
 * @Description: TODO
 * @Author shade.yang
 * @Date 2021/8/25
 * @Version V1.0
 **/
@Getter
@Setter
public class ConsumeTransVo extends AbstractTransVo<PointsConsumeInput, PointsConsumeOutput>{
    private CustomerInfoValidResult.Customer customer;
    private CustomerInfoValidResult.Institution institution;
    private PointsTransDTO pointsTrans;
    private PointsTypeDTO pointsType;

    public ConsumeTransVo(PointsConsumeInput input) {
        super(input);
        this.output = PointsConsumeOutput.builder().build();
        this.customer = input.getCustomerInfo().getCustomer();
        this.institution = input.getCustomerInfo().getInstitution();
    }
}
