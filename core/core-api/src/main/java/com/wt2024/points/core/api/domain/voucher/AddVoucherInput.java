package com.wt2024.points.core.api.domain.voucher;

import com.wt2024.points.core.api.domain.InputBase;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * @ClassName AddVoucherInputDomain
 * @Description: TODO
 * @Author shade.yang
 * @Date 2021/5/21
 * @Version V1.0
 **/
@Getter
@Setter
public class AddVoucherInput extends InputBase {

    @NotEmpty(message = "客户编号不能为空")
    private String customerId;
    @NotNull(message = "凭证不能为空")
    private Voucher voucher;

}
