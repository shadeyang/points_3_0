package com.wt2024.points.restful.backend;

import com.wt2024.points.common.code.PointsCode;
import com.wt2024.points.common.enums.Gender;
import com.wt2024.points.common.enums.VoucherType;
import com.wt2024.points.core.api.domain.customer.CustomerCreateInput;
import com.wt2024.points.core.api.domain.customer.CustomerCreateOutput;
import com.wt2024.points.core.api.domain.customer.CustomerInfoInput;
import com.wt2024.points.core.api.domain.customer.CustomerInfoOutput;
import com.wt2024.points.core.api.domain.voucher.Voucher;
import com.wt2024.points.restful.backend.common.provider.FileArgumentConversion;
import com.wt2024.points.restful.backend.common.provider.FileArgumentSources;
import com.wt2024.points.restful.backend.constant.Constants;
import com.wt2024.points.restful.backend.constant.RestPathConstants;
import com.wt2024.points.restful.backend.domain.ResponseResult;
import com.wt2024.points.restful.backend.tools.generator.*;
import org.junit.jupiter.params.ParameterizedTest;
import org.skyscreamer.jsonassert.JSONAssert;
import org.skyscreamer.jsonassert.JSONCompareMode;

import java.util.ArrayList;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * @author Shade.Yang [shade.yang@aliyun.com]
 * @date 2022/2/7 17:22
 * @project points3.0:com.wt2024.points.restful.backend
 */
public class CustomerTest extends BaseTest {

    @ParameterizedTest
    @FileArgumentSources
    void createCustomer_customers_valid_fail(@FileArgumentConversion CustomerCreateInput customerCreateInput) throws Exception {
        ResponseResult<CustomerCreateOutput> result = HttpPostWithJson.httpPostWithJson(customerCreateInput, RestPathConstants.CUSTOMER.PATH + RestPathConstants.CUSTOMER.CREATE_PATH, CustomerCreateOutput.class);
        assertEquals(PointsCode.TRANS_0047.getCode(), result.getCode(), result.getCode());
    }

    @ParameterizedTest
    @FileArgumentSources
    void createCustomer_success(
            @FileArgumentConversion CustomerInfoInput customerInfoInput,
            @FileArgumentConversion CustomerInfoOutput customerInfoOutput,
            @FileArgumentConversion CustomerCreateInput customerCreateInput) throws Exception {

        setStep("第一步确认客户不存在");
        customerInfoInput.setInstitutionNo(Constants.DEFAULT_INSTITUTION_NO);
        customerInfoInput.setVoucherType(VoucherType.IDENTITY);
        customerInfoInput.setVoucherNo(ChineseIDCardNumberGenerator.getInstance().generate());

        ResponseResult<CustomerInfoOutput> not_exists_result = HttpPostWithJson.httpPostWithJson(customerInfoInput, RestPathConstants.CUSTOMER.PATH + RestPathConstants.CUSTOMER.QUERY_PATH, CustomerInfoOutput.class);
        assertEquals(PointsCode.TRANS_1003.getCode(), not_exists_result.getCode());

        setStep("第二步创建客户");
        customerCreateInput.setInstitutionNo(customerInfoInput.getInstitutionNo());
        customerCreateInput.setCustomerName(ChineseNameGenerator.getInstance().generate());
        customerCreateInput.setAddress(ChineseAddressGenerator.getInstance().generate());
        customerCreateInput.setBirthdate(customerInfoInput.getVoucherNo().substring(6,14));
        customerCreateInput.setEmail(EmailAddressGenerator.getInstance().generate());
        customerCreateInput.setPhoneNumber(ChineseMobileNumberGenerator.getInstance().generate());
        customerCreateInput.setVoucher(new Voucher(){{
            setVoucherType(customerInfoInput.getVoucherType());
            setVoucherNo(customerInfoInput.getVoucherNo());
        }});
        customerCreateInput.setGender(Gender.FEMALE);

        ResponseResult<CustomerCreateOutput> create_result = HttpPostWithJson.httpPostWithJson(customerCreateInput, RestPathConstants.CUSTOMER.PATH + RestPathConstants.CUSTOMER.CREATE_PATH, CustomerCreateOutput.class);
        assertEquals(PointsCode.TRANS_0000.getCode(), create_result.getCode());
        assertNotNull(create_result.getData());
        String customerId = create_result.getData().getCustomerId();
        assertNotNull(customerId);

        setStep("第三步客户已存在");
        customerInfoOutput.setCustomerId(customerId);
        customerInfoOutput.setCustomerName(customerCreateInput.getCustomerName());
        customerInfoOutput.setAddress(customerCreateInput.getAddress());
        customerInfoOutput.setBirthdate(customerCreateInput.getBirthdate());
        customerInfoOutput.setEmail(customerCreateInput.getEmail());
        customerInfoOutput.setPhoneNumber(customerCreateInput.getPhoneNumber());
        customerInfoOutput.setGender(customerCreateInput.getGender());
        customerInfoOutput.setPointsAccountInfoList(new ArrayList<>());

        ResponseResult<CustomerInfoOutput> exists_result = HttpPostWithJson.httpPostWithJson(customerInfoInput, RestPathConstants.CUSTOMER.PATH + RestPathConstants.CUSTOMER.QUERY_PATH, CustomerInfoOutput.class);
        assertEquals(PointsCode.TRANS_0000.getCode(), exists_result.getCode());
        JSONAssert.assertEquals( customerInfoOutput.toString(), exists_result.getData().toString(), JSONCompareMode.NON_EXTENSIBLE);

        setStep("第四步重复创建客户");
        ResponseResult<CustomerCreateOutput> create_again_result = HttpPostWithJson.httpPostWithJson(customerCreateInput, RestPathConstants.CUSTOMER.PATH + RestPathConstants.CUSTOMER.CREATE_PATH, CustomerCreateOutput.class);
        assertEquals(PointsCode.TRANS_0000.getCode(), create_again_result.getCode());
        assertNotNull(create_again_result.getData());
        assertEquals(customerId, create_again_result.getData().getCustomerId());

    }
}
