package com.wt2024.points.core.api.domain.customer;

import com.wt2024.points.common.enums.VoucherType;
import com.wt2024.points.core.api.domain.CustomerInfoBase;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * @ClassName points:com.wt2024.points.service.domain:CustomerInfoInputDomain
 * @Description: TODO
 * @Author shade.yang
 * @Date 2021/4/10
 **/
@Getter
@Setter
public class CustomerInfoInput extends CustomerInfoBase {

    @NotEmpty(message = "凭证编号不能为空")
    private String voucherNo;
    @NotNull(message = "凭证类型不能为空")
    private VoucherType voucherType;

    private String pointsTypeNo;

}
