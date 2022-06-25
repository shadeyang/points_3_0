package com.wt2024.points.repository.customer.converter;

import com.wt2024.points.repository.api.voucher.domain.VoucherDTO;
import com.wt2024.points.repository.customer.entity.Voucher;
import org.mapstruct.Mapper;

/**
 * @ClassName VoucherConverter
 * @Description: TODO
 * @Author shade.yang
 * @Date 2022/1/2
 * @Version V1.0
 **/
@Mapper
public interface VoucherConverter {

    VoucherDTO toVoucherDTO(Voucher voucher);

    Voucher toVoucher(VoucherDTO voucherDTO);
}
