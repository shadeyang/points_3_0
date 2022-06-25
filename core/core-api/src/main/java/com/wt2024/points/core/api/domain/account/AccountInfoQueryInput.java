package com.wt2024.points.core.api.domain.account;

import com.wt2024.points.common.enums.VoucherType;
import com.wt2024.points.core.api.domain.CustomerInfoBase;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * @Author Shade.Yang [shade.yang@aliyun.com]
 * @Date 2021/5/26 15:07
 * @Project points2.0:com.wt2024.points.service.domain.account
 */
@Getter
@Setter
public class AccountInfoQueryInput extends CustomerInfoBase {
    @NotEmpty(message = "凭证编号不能为空")
    private String voucherNo;
    @NotNull(message = "凭证类型不能为空")
    private VoucherType voucherType;
    @NotNull(message = "积分类型不能为空")
    private String pointsTypeNo;

}
