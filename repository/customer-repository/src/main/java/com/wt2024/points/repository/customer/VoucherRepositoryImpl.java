package com.wt2024.points.repository.customer;

import com.wt2024.points.repository.api.voucher.domain.VoucherDTO;
import com.wt2024.points.repository.api.voucher.repository.VoucherRepository;
import com.wt2024.points.repository.customer.converter.ConverterConstants;
import com.wt2024.points.repository.customer.entity.Voucher;
import com.wt2024.points.repository.customer.mapper.VoucherMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @ClassName VoucherRepositoryImpl
 * @Description: TODO
 * @Author shade.yang
 * @Date 2022/1/2
 * @Version V1.0
 **/
@Slf4j
@Repository
public class VoucherRepositoryImpl implements VoucherRepository {

    @Autowired
    VoucherMapper voucherMapper;

    @Override
    public VoucherDTO queryVoucher(String type, String voucherNo) {
        Voucher voucher = new Voucher();
        voucher.setVoucherTypeNo(type);
        voucher.setVoucherNo(voucherNo);
        return ConverterConstants.VOUCHER_CONVERTER.toVoucherDTO(voucherMapper.selectByVoucher(voucher));
    }

    @Override
    public Integer addVoucher(VoucherDTO voucherDTO) {
        return voucherMapper.insert(ConverterConstants.VOUCHER_CONVERTER.toVoucher(voucherDTO));
    }

    @Override
    public List<VoucherDTO> queryVoucherByCustomer(String customerId) {
        return voucherMapper.selectByCustomerId(customerId).stream().map(ConverterConstants.VOUCHER_CONVERTER::toVoucherDTO).collect(Collectors.toList());
    }

    @Override
    public Integer updateVoucher(VoucherDTO voucherDTO) {
        return voucherMapper.updateByVoucher(ConverterConstants.VOUCHER_CONVERTER.toVoucher(voucherDTO));
    }

    @Override
    public Integer deleteVoucher(VoucherDTO voucherDTO) {
        return voucherMapper.deleteByVoucher(ConverterConstants.VOUCHER_CONVERTER.toVoucher(voucherDTO));
    }
}
