package com.wt2024.points.core.api.domain.trans;

import com.wt2024.points.core.api.domain.CustomerInfoBase;
import com.wt2024.points.core.api.domain.voucher.Voucher;
import com.wt2024.points.core.api.validation.annotation.VoucherNoField;
import com.wt2024.points.core.api.validation.annotation.VoucherTypeField;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.Date;

/**
 * @Author Shade.Yang [shade.yang@aliyun.com]
 * @Date 2021/6/4 16:11
 * @Project points2.0:com.wt2024.points.service.domain.trans
 */
@Getter
@Setter
@VoucherNoField(field = "voucher.voucherNo")
@VoucherTypeField(field = "voucher.voucherType")
public class PointsTransQueryListInput extends CustomerInfoBase {

    @NotNull(message = "凭证信息不能为空")
    private Voucher voucher;
    @NotEmpty(message = "积分类型不能为空")
    private String pointsTypeNo;
    /**
     * 开始日期
     */
    private Date fromDate;
    /**
     * 结束日期
     */
    private Date toDate;
    /**
     * 查询页码
     */
    private int index;
    /**
     * 每页记录数。分页查询时，每页显示的记录数
     */
    private int pageSize;

    private long fromId;

}
