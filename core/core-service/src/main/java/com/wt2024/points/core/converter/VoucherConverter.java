package com.wt2024.points.core.converter;

import com.wt2024.points.core.api.domain.voucher.AddVoucherInput;
import com.wt2024.points.core.api.domain.voucher.Voucher;
import com.wt2024.points.repository.api.voucher.domain.VoucherDTO;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

/**
 * @author Shade.Yang [shade.yang@aliyun.com]
 * @date 2022/1/25 10:31
 * @project points3.0:com.wt2024.points.core.converter
 */
@Mapper
public interface VoucherConverter {

    Voucher toVoucher(VoucherDTO voucherDTO);

    @Mapping(source = "voucher.voucherNo", target = "voucherNo")
    @Mapping(source = "voucher.voucherType.type", target = "voucherTypeNo")
    @Mapping(source = "voucher.voucherOpenDate", target = "voucherOpenDate")
    @Mapping(source = "customerId", target = "customerId")
    VoucherDTO toVoucherDTO(AddVoucherInput addVoucherInput);
    
}
