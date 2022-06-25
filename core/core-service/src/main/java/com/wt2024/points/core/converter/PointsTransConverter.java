package com.wt2024.points.core.converter;

import com.wt2024.points.common.enums.*;
import com.wt2024.points.core.api.domain.trans.PointsTrans;
import com.wt2024.points.core.constant.Constants;
import com.wt2024.points.repository.api.account.domain.PointsTransDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * @author Shade.Yang [shade.yang@aliyun.com]
 * @date 2021/12/17 17:21
 * @project points3.0:com.wt2024.points.core.converter
 */
@Mapper
public interface PointsTransConverter {

    @Mapping(source = "transNo", target = "trans.transNo")
    @Mapping(source = "transDate", target = "trans.transDate")
    @Mapping(source = "transTime", target = "trans.transTime")
    @Mapping(source = "transTypeNo", target = "trans.transType")
    @Mapping(source = "oldTransNo", target = "trans.oldTransNo")
    @Mapping(source = "transChannel", target = "trans.transChannel")
    @Mapping(source = "transStatus", target = "trans.transStatus")
    @Mapping(source = "sysTransNo", target = "trans.sysTransNo")
    @Mapping(source = "rulesId", target = "trans.rulesId")

    @Mapping(source = "customerId", target = "points.customerId")
    @Mapping(source = "pointsTypeNo", target = "points.pointsTypeNo")
    @Mapping(source = "pointsAmount", target = "points.pointsAmount")
    @Mapping(source = "endDate", target = "points.endDate")

    @Mapping(source = "voucherTypeNo", target = "voucher.voucherType")
    @Mapping(source = "voucherNo", target = "voucher.voucherNo")
    PointsTrans toPointsTrans(PointsTransDTO pointsTransDTO);

    @Mapping(source = "trans.transNo", target = "transNo")
    @Mapping(source = "trans.transDate", target = "transDate")
    @Mapping(source = "trans.transTime", target = "transTime")
    @Mapping(source = "trans.transType", target = "transTypeNo")
    @Mapping(source = "trans.oldTransNo", target = "oldTransNo")
    @Mapping(source = "trans.transChannel", target = "transChannel")
    @Mapping(source = "trans.transStatus", target = "transStatus")
    @Mapping(source = "trans.sysTransNo", target = "sysTransNo")
    @Mapping(source = "trans.rulesId", target = "rulesId")

    @Mapping(source = "points.customerId", target = "customerId")
    @Mapping(source = "points.pointsTypeNo", target = "pointsTypeNo")
    @Mapping(source = "points.pointsAmount", target = "pointsAmount")
    @Mapping(source = "points.endDate", target = "endDate")

    @Mapping(source = "voucher.voucherType", target = "voucherTypeNo")
    @Mapping(source = "voucher.voucherNo", target = "voucherNo")
    PointsTransDTO toPointsTransDTO(PointsTrans pointsTrans);

    PointsTransDTO clone(PointsTransDTO pointsTransDTO);

    default TransType toTransType(String transTypeNo) {
        return TransType.getEnum(transTypeNo);
    }

    default TransStatus toTransStatus(String transStatus) {
        return TransStatus.getEnum(transStatus);
    }

    default VoucherType toVoucherType(String voucherType) {
        return VoucherType.getEnum(voucherType);
    }

    default DebitOrCredit toDebitOrCredit(String debitOrCredit) {
        return DebitOrCredit.getEnum(debitOrCredit);
    }

    default ReversedFlag toReversedFlag(String reversedFlag) {
        return ReversedFlag.getEnum(reversedFlag);
    }

    default String toEndDate(Date endDate) {
        return endDate == null ? null : new SimpleDateFormat(Constants.DATE_TIME_FORMATTER_YYYYMMDD_HHMMSS).format(endDate);
    }

    default Date toEndDate(String endDate) {
        try {
            return new SimpleDateFormat(Constants.DATE_TIME_FORMATTER_YYYYMMDD_HHMMSS).parse(endDate);
        } catch (ParseException e) {
            return null;
        }
    }
}
