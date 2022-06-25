package com.wt2024.points.core.service;

import com.wt2024.points.core.api.domain.account.*;
import com.wt2024.points.core.api.domain.trans.PointsTrans;
import com.wt2024.points.core.api.domain.valid.CustomerInfoValidResult;
import com.wt2024.points.repository.api.account.domain.PointsAccountDetailsDTO;
import com.wt2024.points.repository.api.account.domain.PointsAccountInfoDTO;
import com.wt2024.points.repository.api.account.domain.PointsTransDTO;
import com.wt2024.points.repository.api.account.repository.PointsAccountInfoRepository;
import com.wt2024.points.repository.api.account.repository.PointsTransRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.when;

/**
 * @author Shade.Yang [shade.yang@aliyun.com]
 * @date 2021/12/17 17:39
 * @project points3.0:com.wt2024.points.core.service
 */
@ExtendWith(MockitoExtension.class)
class AccountServiceImplTest {

    @InjectMocks
    AccountServiceImpl accountService;

    @Mock
    PointsAccountInfoRepository pointsAccountInfoRepository;
    @Mock
    PointsTransRepository pointsTransRepository;

    @BeforeAll
    static void beforeAll() {
        MockitoAnnotations.openMocks(AccountServiceImpl.class);
    }

    @Test
    void queryAllAccountInfo() {
        PointsAccountInfoDTO pointsAccountInfoDTO = new PointsAccountInfoDTO() {{
            setFreezingPoints(new BigDecimal(20));
            setPointsAccountStatus("status");
            setInTransitPoints(new BigDecimal(30));
            setCustomerId("customer");
            setPointsBalance(new BigDecimal(40));
            setPointsTypeNo("type");
        }};

        AccountInfoQueryAllInput accountInfoQueryAllInput = new AccountInfoQueryAllInput() {{
            setCustomerInfo(new CustomerInfoValidResult() {{
                setInstitution(new Institution() {{
                    setInstitutionId("institutionId");
                }});
                setCustomer(new Customer() {{
                    setCustomerId(pointsAccountInfoDTO.getCustomerId());
                }});
            }});
        }};

        when(pointsAccountInfoRepository.queryPointsAccountInfo(anyString(), eq(null), anyString())).thenReturn(new ArrayList<>() {{
            add(pointsAccountInfoDTO);
        }});

        AccountInfoQueryAllOutput accountInfoQueryAllOutput = accountService.queryAllAccountInfo(accountInfoQueryAllInput);
        Assertions.assertEquals(1, accountInfoQueryAllOutput.getPointsAccountInfoList().size());
        PointsAccountInfo pointsAccountInfo = accountInfoQueryAllOutput.getPointsAccountInfoList().get(0);
        Assertions.assertAll(
                () -> Assertions.assertEquals(pointsAccountInfoDTO.getPointsAccountStatus(), pointsAccountInfo.getPointsAccountStatus()),
                () -> Assertions.assertEquals(pointsAccountInfoDTO.getFreezingPoints(), pointsAccountInfo.getFreezingPoints()),
                () -> Assertions.assertEquals(pointsAccountInfoDTO.getPointsTypeNo(), pointsAccountInfo.getPointsTypeNo()),
                () -> Assertions.assertEquals(pointsAccountInfoDTO.getPointsBalance(), pointsAccountInfo.getPointsBalance()),
                () -> Assertions.assertEquals(pointsAccountInfoDTO.getPointsBalance(), pointsAccountInfo.getPointsBalance()),
                () -> Assertions.assertEquals(pointsAccountInfoDTO.getInTransitPoints(), pointsAccountInfo.getInTransitPoints())
        );
    }

    @Test
    void queryAccountInfoByPointsType() {
        PointsAccountInfoDTO pointsAccountInfoDTO = new PointsAccountInfoDTO() {{
            setFreezingPoints(new BigDecimal(20));
            setPointsAccountStatus("status");
            setInTransitPoints(new BigDecimal(30));
            setCustomerId("customer");
            setPointsBalance(new BigDecimal(40));
            setPointsTypeNo("type");
        }};
        AccountInfoQueryInput accountInfoQueryInput = new AccountInfoQueryInput() {{
            setPointsTypeNo("pointsTypeNo");
            setCustomerInfo(new CustomerInfoValidResult() {{
                setInstitution(new Institution() {{
                    setInstitutionId("institutionId");
                }});
                setCustomer(new Customer() {{
                    setCustomerId(pointsAccountInfoDTO.getCustomerId());
                }});
            }});
        }};
        when(pointsAccountInfoRepository.queryPointsAccountInfo(anyString(), eq(accountInfoQueryInput.getPointsTypeNo()), anyString())).thenReturn(new ArrayList<>() {{
            add(pointsAccountInfoDTO);
        }});

        AccountInfoQueryOutput accountInfoQueryOutput = accountService.queryAccountInfoByPointsType(accountInfoQueryInput);
        Assertions.assertNotNull(accountInfoQueryOutput.getPointsAccountInfo());
        PointsAccountInfo pointsAccountInfo = accountInfoQueryOutput.getPointsAccountInfo();
        Assertions.assertAll(
                () -> Assertions.assertEquals(pointsAccountInfoDTO.getPointsAccountStatus(), pointsAccountInfo.getPointsAccountStatus()),
                () -> Assertions.assertEquals(pointsAccountInfoDTO.getFreezingPoints(), pointsAccountInfo.getFreezingPoints()),
                () -> Assertions.assertEquals(pointsAccountInfoDTO.getPointsTypeNo(), pointsAccountInfo.getPointsTypeNo()),
                () -> Assertions.assertEquals(pointsAccountInfoDTO.getPointsBalance(), pointsAccountInfo.getPointsBalance()),
                () -> Assertions.assertEquals(pointsAccountInfoDTO.getPointsBalance(), pointsAccountInfo.getPointsBalance()),
                () -> Assertions.assertEquals(pointsAccountInfoDTO.getInTransitPoints(), pointsAccountInfo.getInTransitPoints())
        );
    }

    @Test
    void queryAccountDetails() {
        PointsAccountInfoDTO pointsAccountInfoDTO = new PointsAccountInfoDTO() {{
            setFreezingPoints(new BigDecimal(20));
            setPointsAccountStatus("status");
            setInTransitPoints(new BigDecimal(30));
            setCustomerId("customer");
            setPointsBalance(new BigDecimal(40));
            setPointsTypeNo("type");
        }};

        AccountDetailsQueryInput accountDetailsQueryInput = new AccountDetailsQueryInput() {{
            setPointsTypeNo("pointsTypeNo");
            setCustomerInfo(new CustomerInfoValidResult() {{
                setInstitution(new Institution() {{
                    setInstitutionId("institutionId");
                }});
                setCustomer(new Customer() {{
                    setCustomerId(pointsAccountInfoDTO.getCustomerId());
                }});
            }});
        }};

        PointsTransDTO pointsTransDTO = new PointsTransDTO() {{
            setPointsTypeNo(accountDetailsQueryInput.getPointsTypeNo());
            setPointsAmount(new BigDecimal(100));
        }};
        PointsAccountDetailsDTO pointsAccountDetailsDTO = new PointsAccountDetailsDTO() {{
            setPointsAmount(new BigDecimal(100));
            setExpirationTime(new Date());
        }};

        when(pointsTransRepository.queryAccountingTrans(eq(pointsAccountInfoDTO.getCustomerId()), eq(accountDetailsQueryInput.getPointsTypeNo()))).thenReturn(new ArrayList<>() {{
            add(pointsTransDTO);
        }});
        when(pointsTransRepository.queryPointsAccountDetails(eq(pointsAccountInfoDTO.getCustomerId()), eq(accountDetailsQueryInput.getPointsTypeNo()), any(List.class))).thenReturn(new ArrayList<>() {{
            add(pointsAccountDetailsDTO);
        }});

        AccountDetailsQueryOutput accountDetailsQueryOutput = accountService.queryAccountDetails(accountDetailsQueryInput);

        Assertions.assertNotNull(accountDetailsQueryOutput.getPointsAccountDetailsList());
        Assertions.assertEquals(1, accountDetailsQueryOutput.getPointsAccountDetailsList().size());
        Assertions.assertNotNull(accountDetailsQueryOutput.getPointsTransList());
        Assertions.assertEquals(1, accountDetailsQueryOutput.getPointsTransList().size());
        PointsAccountDetails pointsAccountDetails = accountDetailsQueryOutput.getPointsAccountDetailsList().get(0);
        PointsTrans pointsTrans = accountDetailsQueryOutput.getPointsTransList().get(0);
        Assertions.assertAll(
                () -> Assertions.assertEquals(pointsAccountDetailsDTO.getExpirationTime(), pointsAccountDetails.getExpirationTime()),
                () -> Assertions.assertEquals(pointsAccountDetailsDTO.getPointsAmount(), pointsAccountDetails.getPointsAmount()),
                () -> Assertions.assertEquals(pointsTransDTO.getPointsTypeNo(), pointsTrans.getPoints().getPointsTypeNo()),
                () -> Assertions.assertEquals(pointsTransDTO.getPointsAmount(), pointsTrans.getPoints().getPointsAmount())
        );
    }
}