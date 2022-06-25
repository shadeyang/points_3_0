package com.wt2024.points.core.api.domain.voucher;

import com.wt2024.points.common.enums.VoucherType;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * @ClassName VoucherDomain
 * @Description: TODO
 * @Author shade.yang
 * @Date 2021/5/21
 * @Version V1.0
 **/
@Getter
@Setter
public class Voucher implements Serializable {

    @NotNull(message = "凭证类型不能为空")
    private VoucherType voucherType;
    @NotEmpty(message = "凭证编号不能为空")
    private String voucherNo;

    private String voucherOpenDate;

}
