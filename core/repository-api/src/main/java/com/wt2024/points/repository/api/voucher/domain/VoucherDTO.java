package com.wt2024.points.repository.api.voucher.domain;

import lombok.Getter;
import lombok.Setter;

/**
 * @ClassName VoucherDTO
 * @Description: TODO
 * @Author shade.yang
 * @Date 2022/1/1
 * @Version V1.0
 **/
@Getter
@Setter
public class VoucherDTO {

    private String customerId;

    private String voucherTypeNo;

    private String voucherNo;

    private String voucherOpenDate;

}
