package com.wt2024.points.core.api.domain.trans;

import com.wt2024.points.common.enums.DebitOrCredit;
import com.wt2024.points.common.enums.TransType;
import com.wt2024.points.core.api.domain.CustomerInfoBase;
import com.wt2024.points.core.api.domain.voucher.Voucher;
import com.wt2024.points.core.api.validation.annotation.VoucherNoField;
import com.wt2024.points.core.api.validation.annotation.VoucherTypeField;
import lombok.Getter;
import lombok.Setter;

import javax.validation.Valid;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

/**
 * @Author Shade.Yang [shade.yang@aliyun.com]
 * @Date 2021/8/16 14:50
 * @Project points2.0:com.wt2024.points.core.api.domain.trans
 */
@Getter
@Setter
@VoucherTypeField(field = "voucher.voucherType")
@VoucherNoField(field = "voucher.voucherNo")
public class PointsAccTransInput extends CustomerInfoBase {

    @NotNull(message = "凭证信息不能为空")
    @Valid
    private Voucher voucher;
    @NotNull(message = "积分信息不能为空")
    @Valid
    private Points points;
    @NotNull(message = "交易借贷类型不能为空")
    private DebitOrCredit debitOrCredit;
    @NotNull(message = "积分交易类型不能为空")
    private TransType transType;

    private String description;

    @NotEmpty(message = "发起流水号不能为空")
    private String sysTransNo;

    private String merchantNo;

    private String operator;

    private String costLine;

}
