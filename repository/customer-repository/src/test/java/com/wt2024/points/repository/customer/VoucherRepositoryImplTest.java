package com.wt2024.points.repository.customer;

import com.wt2024.points.common.enums.VoucherType;
import com.wt2024.points.repository.api.voucher.domain.VoucherDTO;
import com.wt2024.points.repository.api.voucher.repository.VoucherRepository;
import com.wt2024.points.repository.customer.entity.Voucher;
import com.wt2024.points.repository.customer.mapper.VoucherMapper;
import org.h2.tools.RunScript;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.sql.DataSource;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class VoucherRepositoryImplTest {

    @Autowired
    VoucherRepository voucherRepository;
    @Autowired
    VoucherMapper voucherMapper;
    @Autowired
    private DataSource dataSource;

    @BeforeEach
    void setUp() throws SQLException, FileNotFoundException {
        RunScript.execute(dataSource.getConnection(), new FileReader("src/test/resources/db_script/voucher.sql"));
    }

    @Test
    void queryVoucher_voucher_not_exists() {
        VoucherDTO voucherDTO = voucherRepository.queryVoucher(VoucherType.IDENTITY.getType(), "not_exists");
        assertNull(voucherDTO);
    }

    @Test
    void queryVoucher_voucher_exists() {
        VoucherDTO voucherDTO = voucherRepository.queryVoucher(VoucherType.IDENTITY.getType(), "1101011990d3d7053X");
        Voucher voucher = voucherMapper.selectByVoucher(new Voucher() {{
            setVoucherTypeNo(VoucherType.IDENTITY.getType());
            setVoucherNo("1101011990d3d7053X");
        }});
        assertNotNull(voucherDTO);
        assertAll(
                () -> assertEquals(voucher.getVoucherNo(), voucherDTO.getVoucherNo()),
                () -> assertEquals(voucher.getVoucherTypeNo(), voucherDTO.getVoucherTypeNo()),
                () -> assertEquals(voucher.getVoucherOpenDate(), voucherDTO.getVoucherOpenDate()),
                () -> assertEquals(voucher.getCustomerId(), voucherDTO.getCustomerId())
        );
    }

    @Test
    void addVoucher() {
        VoucherDTO voucherDTO = new VoucherDTO() {{
            setVoucherTypeNo(VoucherType.EMAIL.getType());
            setCustomerId("844669109487534080");
            setVoucherOpenDate("20211231");
            setVoucherNo("123@143.com");
        }};
        Integer result = voucherRepository.addVoucher(voucherDTO);
        assertEquals(1, result);

        Voucher voucher = voucherMapper.selectByVoucher(new Voucher() {{
            setVoucherTypeNo(voucherDTO.getVoucherTypeNo());
            setVoucherNo(voucherDTO.getVoucherNo());
        }});
        assertNotNull(voucher);
        assertAll(
                () -> assertEquals(voucher.getVoucherNo(), voucherDTO.getVoucherNo()),
                () -> assertEquals(voucher.getVoucherTypeNo(), voucherDTO.getVoucherTypeNo()),
                () -> assertEquals(voucher.getVoucherOpenDate(), voucherDTO.getVoucherOpenDate()),
                () -> assertEquals(voucher.getCustomerId(), voucherDTO.getCustomerId())
        );
    }
}