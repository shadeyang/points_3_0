package com.wt2024.points.repository.api.voucher.repository;

import com.wt2024.points.repository.api.voucher.domain.VoucherDTO;

import java.util.List;

/**
 * @ClassName VoucherRepository
 * @Description: TODO
 * @Author shade.yang
 * @Date 2022/1/1
 * @Version V1.0
 **/
public interface VoucherRepository {
    VoucherDTO queryVoucher(String type, String voucherNo);

    Integer addVoucher(VoucherDTO voucherDTO);

    List<VoucherDTO> queryVoucherByCustomer(String customerId);

    Integer updateVoucher(VoucherDTO voucherDTO);

    Integer deleteVoucher(VoucherDTO voucherDTO);
}
