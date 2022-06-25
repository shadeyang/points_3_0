package com.wt2024.points.repository.customer;

import com.wt2024.points.common.enums.Gender;
import com.wt2024.points.common.enums.VoucherType;
import com.wt2024.points.repository.api.customer.domain.CustomerDTO;
import com.wt2024.points.repository.api.customer.repository.CustomerRepository;
import com.wt2024.points.repository.api.voucher.domain.VoucherDTO;
import com.wt2024.points.repository.customer.entity.Customer;
import com.wt2024.points.repository.customer.entity.Voucher;
import com.wt2024.points.repository.customer.mapper.CustomerMapper;
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
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class CustomerRepositoryImplTest {

    @Autowired
    CustomerRepository customerRepository;

    @Autowired
    CustomerMapper customerMapper;
    @Autowired
    VoucherMapper voucherMapper;

    @Autowired
    private DataSource dataSource;

    @BeforeEach
    void setUp() throws SQLException, FileNotFoundException {
        RunScript.execute(dataSource.getConnection(), new FileReader("src/test/resources/db_script/customer.sql"));
        RunScript.execute(dataSource.getConnection(), new FileReader("src/test/resources/db_script/voucher.sql"));
    }

    @Test
    void queryCustomerById_customer_exists() {
        CustomerDTO customerDTO = customerRepository.queryCustomerById("844669109487534080");
        Customer customer = customerMapper.selectByPrimaryKey("844669109487534080");
        assertAll(
                () -> assertEquals(customer.getCustomerId(), customerDTO.getCustomerId()),
                () -> assertEquals(customer.getCustomerLvl(), customerDTO.getCustomerLvl()),
                () -> assertEquals(customer.getCustomerName(), customerDTO.getCustomerName()),
                () -> assertEquals(customer.getAddress(), customerDTO.getAddress()),
                () -> assertEquals(customer.getEmail(), customerDTO.getEmail()),
                () -> assertEquals(customer.getBirthdate(), customerDTO.getBirthdate()),
                () -> assertEquals(customer.getGender(), customerDTO.getGender()),
                () -> assertEquals(customer.getInstitutionId(), customerDTO.getInstitutionId()),
                () -> assertEquals(customer.getOperator(), customerDTO.getOperator()),
                () -> assertEquals(customer.getPhone(), customerDTO.getPhoneNumber())
        );
    }

    @Test
    void queryCustomerById_customer_not_exists() {
        CustomerDTO customerDTO = customerRepository.queryCustomerById("not_exists");
        assertNull(customerDTO);
    }

    @Test
    void createCustomer() {
        CustomerDTO customerDTO = new CustomerDTO() {{
            setCustomerLvl("0");
            setCustomerName("二麻子");
            setAddress("地址");
            setBirthdate("20000228");
            setEmail("email@email.email");
            setGender(Gender.FEMALE.getDesc());
            setInstitutionId("844669109470756864");
            setOperator("system");
            setPhoneNumber("13512345678");
        }};

        VoucherDTO voucherDTO = new VoucherDTO() {{
            setVoucherTypeNo(VoucherType.EMAIL.getType());
            setVoucherOpenDate("20211231");
            setVoucherNo("123@143.com");
        }};

        CustomerDTO newCustomerDTO = customerRepository.createCustomer(customerDTO, voucherDTO);

        assertNotNull(newCustomerDTO.getCustomerId());

        Customer customer = customerMapper.selectByPrimaryKey(newCustomerDTO.getCustomerId());
        assertNotNull(customer);
        assertAll(
                () -> assertEquals(customer.getCustomerId(), customerDTO.getCustomerId()),
                () -> assertEquals(customer.getCustomerLvl(), customerDTO.getCustomerLvl()),
                () -> assertEquals(customer.getCustomerName(), customerDTO.getCustomerName()),
                () -> assertEquals(customer.getAddress(), customerDTO.getAddress()),
                () -> assertEquals(customer.getEmail(), customerDTO.getEmail()),
                () -> assertEquals(customer.getBirthdate(), customerDTO.getBirthdate()),
                () -> assertEquals(customer.getGender(), customerDTO.getGender()),
                () -> assertEquals(customer.getInstitutionId(), customerDTO.getInstitutionId()),
                () -> assertEquals(customer.getOperator(), customerDTO.getOperator()),
                () -> assertEquals(customer.getPhone(), customerDTO.getPhoneNumber())
        );

        Voucher voucher = voucherMapper.selectByVoucher(new Voucher() {{
            setVoucherTypeNo(voucherDTO.getVoucherTypeNo());
            setVoucherNo(voucherDTO.getVoucherNo());
        }});
        assertNotNull(voucher);
        assertAll(
                () -> assertEquals(voucher.getVoucherNo(), voucherDTO.getVoucherNo()),
                () -> assertEquals(voucher.getVoucherTypeNo(), voucherDTO.getVoucherTypeNo()),
                () -> assertEquals(voucher.getVoucherOpenDate(), voucherDTO.getVoucherOpenDate()),
                () -> assertEquals(voucher.getCustomerId(), newCustomerDTO.getCustomerId())
        );
    }

    @Test
    void selectCustomerList(){
        CustomerDTO customer = new CustomerDTO() {{
            setCustomerId("not_exists");
            setGender(Gender.FEMALE.getType());
        }};
        List<CustomerDTO>  customerDTOList = customerRepository.queryCustomerList(customer);
        assertTrue(customerDTOList.isEmpty());
    }

}