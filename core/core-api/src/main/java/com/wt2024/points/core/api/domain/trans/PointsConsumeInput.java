package com.wt2024.points.core.api.domain.trans;

import com.wt2024.points.core.api.domain.CustomerInfoBase;
import com.wt2024.points.core.api.domain.voucher.Voucher;
import com.wt2024.points.core.api.validation.annotation.VoucherNoField;
import com.wt2024.points.core.api.validation.annotation.VoucherTypeField;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * @Author Shade.Yang [shade.yang@aliyun.com]
 * @Date 2021/6/18 16:12
 * @Project points2.0:com.wt2024.points.service.domain.trans
 */
@Getter
@Setter
@VoucherTypeField(field = "voucher.voucherType")
@VoucherNoField(field = "voucher.voucherNo")
public class PointsConsumeInput extends CustomerInfoBase {
    @NotNull(message = "凭证信息不能为空")
    private Voucher voucher;
    @NotNull(message = "积分信息不能为空")
    private Points points;
    @NotEmpty(message = "发起流水号不能为空")
    private String sysTransNo;
    @NotEmpty(message = "商户号不能为空")
    private String merchantNo;

    private String operator;

    private String description;

}
