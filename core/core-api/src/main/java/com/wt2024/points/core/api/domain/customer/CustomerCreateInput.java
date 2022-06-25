package com.wt2024.points.core.api.domain.customer;

import com.wt2024.points.common.enums.Gender;
import com.wt2024.points.core.api.domain.InputBase;
import com.wt2024.points.core.api.domain.voucher.Voucher;
import lombok.Getter;
import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * @ClassName CustomerCreateInputDomain
 * @Description: TODO
 * @Author shade.yang
 * @Date 2021/5/8
 * @Version V1.0
 **/
@Getter
@Setter
public class CustomerCreateInput extends InputBase {

    @NotEmpty(message = "客户名称不能为空")
    private String customerName;
    @NotNull(message = "性别不能为空")
    private Gender gender;
    private String phoneNumber;
    private String email;
    private String address;
    private String birthdate;
    @Valid
    private Voucher voucher;

}
