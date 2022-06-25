package com.wt2024.points.restful.backend;

import com.wt2024.points.common.code.PointsCode;
import com.wt2024.points.common.enums.Gender;
import com.wt2024.points.common.enums.VoucherType;
import com.wt2024.points.core.api.domain.customer.CustomerCreateInput;
import com.wt2024.points.core.api.domain.customer.CustomerCreateOutput;
import com.wt2024.points.core.api.domain.voucher.AddVoucherInput;
import com.wt2024.points.core.api.domain.voucher.Voucher;
import com.wt2024.points.restful.backend.common.provider.FileArgumentConversion;
import com.wt2024.points.restful.backend.common.provider.FileArgumentSources;
import com.wt2024.points.restful.backend.constant.Constants;
import com.wt2024.points.restful.backend.constant.RestPathConstants;
import com.wt2024.points.restful.backend.domain.ResponseResult;
import com.wt2024.points.restful.backend.tools.generator.*;
import org.junit.jupiter.params.ParameterizedTest;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * @author Shade.Yang [shade.yang@aliyun.com]
 * @date 2022/2/9 16:57
 * @project points3.0:com.wt2024.points.restful.backend
 */
public class VoucherTest extends BaseTest {

    @ParameterizedTest
    @FileArgumentSources
    void addVoucher_success(@FileArgumentConversion CustomerCreateInput customerCreateInput,
                            @FileArgumentConversion AddVoucherInput addVoucherInput) throws Exception {
        setStep("第一步确认客户");
        customerCreateInput.setInstitutionNo(Constants.DEFAULT_INSTITUTION_NO);
        customerCreateInput.setCustomerName(ChineseNameGenerator.getInstance().generate());
        customerCreateInput.setAddress(ChineseAddressGenerator.getInstance().generate());
        customerCreateInput.setEmail(EmailAddressGenerator.getInstance().generate());
        customerCreateInput.setPhoneNumber(ChineseMobileNumberGenerator.getInstance().generate());
        customerCreateInput.setVoucher(new Voucher(){{
            setVoucherType(VoucherType.IDENTITY);
            setVoucherNo(ChineseIDCardNumberGenerator.getInstance().generate());
        }});
        customerCreateInput.setBirthdate(customerCreateInput.getVoucher().getVoucherNo().substring(6,14));
        customerCreateInput.setGender(Gender.FEMALE);

        ResponseResult<CustomerCreateOutput> create_result = HttpPostWithJson.httpPostWithJson(customerCreateInput, RestPathConstants.CUSTOMER.PATH + RestPathConstants.CUSTOMER.CREATE_PATH, CustomerCreateOutput.class);
        assertEquals(PointsCode.TRANS_0000.getCode(), create_result.getCode());
        assertNotNull(create_result.getData());
        String customerId = create_result.getData().getCustomerId();
        assertNotNull(customerId);

        setStep("第二步加挂凭证");
        addVoucherInput.setInstitutionNo(customerCreateInput.getInstitutionNo());
        addVoucherInput.setCustomerId(customerId);
        addVoucherInput.setVoucher(new Voucher(){{
            setVoucherNo(customerCreateInput.getPhoneNumber());
            setVoucherType(VoucherType.MOBILE);
            setVoucherOpenDate(LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE));
        }});
        ResponseResult add_voucher_result = HttpPostWithJson.httpPostWithJson(addVoucherInput, RestPathConstants.VOUCHER.PATH + RestPathConstants.VOUCHER.ADD_PATH, Object.class);
        assertEquals(PointsCode.TRANS_0000.getCode(), add_voucher_result.getCode());

        setStep("第三步重复加挂凭证");
        ResponseResult add_voucher_again_result = HttpPostWithJson.httpPostWithJson(addVoucherInput, RestPathConstants.VOUCHER.PATH + RestPathConstants.VOUCHER.ADD_PATH, Object.class);
        assertEquals(PointsCode.TRANS_1002.getCode(), add_voucher_again_result.getCode());
    }
}
