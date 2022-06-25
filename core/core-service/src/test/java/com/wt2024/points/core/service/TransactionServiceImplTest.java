package com.wt2024.points.core.service;

import com.wt2024.points.common.code.PointsCode;
import com.wt2024.points.common.enums.*;
import com.wt2024.points.common.exception.PointsException;
import com.wt2024.points.common.sequence.Sequence;
import com.wt2024.points.core.api.domain.CustomerInfoBase;
import com.wt2024.points.core.api.domain.trans.*;
import com.wt2024.points.core.api.domain.valid.CustomerInfoValidResult;
import com.wt2024.points.core.api.domain.voucher.Voucher;
import com.wt2024.points.core.constant.Constants;
import com.wt2024.points.core.handler.trans.AccountTransHandler;
import com.wt2024.points.core.handler.trans.ConsumeTransHandler;
import com.wt2024.points.core.handler.trans.ReverseTransHandler;
import com.wt2024.points.repository.api.account.domain.*;
import com.wt2024.points.repository.api.account.repository.PointsAccountInfoRepository;
import com.wt2024.points.repository.api.account.repository.PointsCostRepository;
import com.wt2024.points.repository.api.account.repository.PointsTransRepository;
import com.wt2024.points.repository.api.account.repository.PointsTypeRepository;
import com.wt2024.points.repository.api.cache.repository.CacheRepository;
import com.wt2024.points.repository.api.merchant.domain.MerchantDTO;
import com.wt2024.points.repository.api.merchant.repository.MerchantRepository;
import com.wt2024.points.repository.api.system.domain.InstitutionDTO;
import com.wt2024.points.repository.api.system.repository.InstitutionRepository;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.ApplicationContext;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

/**
 * @author Shade.Yang [shade.yang@aliyun.com]
 * @date 2022/1/26 08:52
 * @project points3.0:com.wt2024.points.core.service
 */
@ExtendWith(MockitoExtension.class)
class TransactionServiceImplTest {

    @InjectMocks
    TransactionServiceImpl transactionService;

    @InjectMocks
    ConsumeTransHandler consumeTransHandler;
    @InjectMocks
    AccountTransHandler accountTransHandler;
    @InjectMocks
    ReverseTransHandler reverseTransHandler;

    @Mock
    PointsTransRepository pointsTransRepository;
    @Mock
    PointsAccountInfoRepository pointsAccountInfoRepository;
    @Mock
    PointsTypeRepository pointsTypeRepository;
    @Mock
    InstitutionRepository institutionRepository;
    @Mock
    CacheRepository cacheRepository;
    @Mock
    ApplicationContext applicationContext;
    @Mock
    MerchantRepository merchantRepository;
    @Mock
    PointsCostRepository pointsCostRepository;

    @BeforeAll
    static void beforeAll() {
        MockitoAnnotations.openMocks(TransactionServiceImpl.class);
        MockitoAnnotations.openMocks(ConsumeTransHandler.class);
        MockitoAnnotations.openMocks(AccountTransHandler.class);
        MockitoAnnotations.openMocks(ReverseTransHandler.class);
    }

    @BeforeEach
    void setUp() {
        transactionService.setConsumeTransHandler(consumeTransHandler);
        transactionService.setAccountTransHandler(accountTransHandler);
        transactionService.setReverseTransHandler(reverseTransHandler);
    }

    @Test
    void queryPointsTransList() {

        PointsTransDTO pointsTransDTO = new PointsTransDTO() {{
            setId(111222L);
            setPointsTypeNo("pointsTypeNo");
            setPointsAmount(BigDecimal.ONE);
            setInstitutionId("institutionId");
            setDebitOrCredit(DebitOrCredit.CREDIT.getCode());
            setDescription("description");
            setEndDate(new Date());
            setMerchantNo("merchantNo");
            setOldTransNo("oldTransNo");
            setOperator("operator");
            setReversedFlag(ReversedFlag.REVERSED.getCode());
            setRulesId(2);
            setSysTransNo("sysTransNo");
            setTransChannel("transChannel");
            setTransDate("transDate");
            setTransNo("transNo");
            setTransStatus(TransStatus.FAILED.getCode());
            setTransTime("transTime");
            setTransTypeNo(TransType.ACTIVITY_POINT.getCode());
            setTransTimestamp(System.currentTimeMillis());
            setVoucherNo("voucherNo");
            setVoucherTypeNo(VoucherType.EMAIL.getType());
            setClearingAmt(BigDecimal.ZERO);
            setCostLine("4");
            setCustomerId("customerId");
        }};

        PointsTransQueryListInput pointsTransQueryListInput = new PointsTransQueryListInput() {{
            setCustomerInfo(new CustomerInfoValidResult() {{
                setInstitution(new Institution() {{
                    setInstitutionId(pointsTransDTO.getInstitutionId());
                }});
                setCustomer(new Customer() {{
                    setCustomerId(pointsTransDTO.getCustomerId());
                }});
                setVoucher(new Voucher() {{
                    setVoucherNo(pointsTransDTO.getVoucherNo());
                    setVoucherType(VoucherType.getEnum(pointsTransDTO.getVoucherTypeNo()));
                }});

                setPointsTypeNo(pointsTransDTO.getPointsTypeNo());
                setInstitutionNo("institutionNo");
            }});
        }};

        when(pointsTransRepository.queryPointsTransByPage(anyString(), anyString(), anyString(), anyLong(), eq(null), eq(null), anyInt(), anyInt())).thenReturn(new ArrayList<>() {{
            add(pointsTransDTO);
        }});
        PointsTransQueryListOutput output = transactionService.queryPointsTransList(pointsTransQueryListInput);
        assertNotNull(output);
        assertEquals(1, output.getPointsTransList().size());
        PointsTrans pointsTrans = output.getPointsTransList().get(0);
        assertAll(
                () -> assertEquals(pointsTransDTO.getPointsTypeNo(), pointsTrans.getPoints().getPointsTypeNo()),
                () -> assertEquals(pointsTransDTO.getPointsAmount(), pointsTrans.getPoints().getPointsAmount()),
                () -> assertEquals(pointsTransDTO.getCustomerId(), pointsTrans.getPoints().getCustomerId()),
                () -> assertEquals(new SimpleDateFormat(Constants.DATE_TIME_FORMATTER_YYYYMMDD_HHMMSS).format(pointsTransDTO.getEndDate()), pointsTrans.getPoints().getEndDate()),

                () -> assertEquals(pointsTransDTO.getTransNo(), pointsTrans.getTrans().getTransNo()),
                () -> assertEquals(pointsTransDTO.getSysTransNo(), pointsTrans.getTrans().getSysTransNo()),
                () -> assertEquals(pointsTransDTO.getOldTransNo(), pointsTrans.getTrans().getOldTransNo()),
                () -> assertEquals(pointsTransDTO.getTransDate(), pointsTrans.getTrans().getTransDate()),
                () -> assertEquals(pointsTransDTO.getTransTime(), pointsTrans.getTrans().getTransTime()),
                () -> assertEquals(pointsTransDTO.getTransChannel(), pointsTrans.getTrans().getTransChannel()),
                () -> assertEquals(pointsTransDTO.getTransStatus(), pointsTrans.getTrans().getTransStatus().getCode()),
                () -> assertEquals(pointsTransDTO.getRulesId(), pointsTrans.getTrans().getRulesId()),
                () -> assertEquals(pointsTransDTO.getTransTypeNo(), pointsTrans.getTrans().getTransType().getCode()),

                () -> assertEquals(pointsTransDTO.getVoucherNo(), pointsTrans.getVoucher().getVoucherNo()),
                () -> assertEquals(pointsTransDTO.getVoucherTypeNo(), pointsTrans.getVoucher().getVoucherType().getType()),

                () -> assertEquals(pointsTransDTO.getId(), pointsTrans.getId()),
                () -> assertEquals(pointsTransDTO.getTransNo(), pointsTrans.getTransNo()),
                () -> assertEquals(pointsTransDTO.getDescription(), pointsTrans.getDescription()),
                () -> assertEquals(pointsTransDTO.getDebitOrCredit(), pointsTrans.getDebitOrCredit().getCode()),
                () -> assertEquals(pointsTransDTO.getInstitutionId(), pointsTrans.getInstitutionId()),
                () -> assertEquals(pointsTransDTO.getMerchantNo(), pointsTrans.getMerchantNo()),
                () -> assertEquals(pointsTransDTO.getOperator(), pointsTrans.getOperator()),
                () -> assertEquals(pointsTransDTO.getReversedFlag(), pointsTrans.getReversedFlag().getCode())
        );
    }

    @Test
    void consumePoints_points_type_not_exists() {
        PointsConsumeInput input = getPointsConsumeInput();

        when(pointsTypeRepository.queryPointsTypeByInst(eq(input.getPoints().getPointsTypeNo()), eq(input.getCustomerInfo().getInstitutionId()))).thenReturn(null);
        when(cacheRepository.lock(any(String.class), any(Long.class), any(TimeUnit.class))).thenReturn(true);

        PointsException exception = assertThrows(PointsException.class, () -> transactionService.consumePoints(input));
        assertEquals(PointsCode.TRANS_2005.getCode(), exception.getPointsCode().getCode());
        verify(pointsTransRepository, never()).processTrans(any(PointsTransDTO.class), any(PointsTransDTO.class));
    }
    @Test
    void consumePoints_merchant_invalid() {
        PointsConsumeInput input = getPointsConsumeInput();
        PointsTypeDTO pointsType = getPointsTypeDTO(input);

        when(pointsTypeRepository.queryPointsTypeByInst(eq(input.getPoints().getPointsTypeNo()), eq(input.getCustomerInfo().getInstitutionId()))).thenReturn(pointsType);
        when(cacheRepository.lock(any(String.class), any(Long.class), any(TimeUnit.class))).thenReturn(true);
        PointsException exception = assertThrows(PointsException.class, () -> transactionService.consumePoints(input));
        assertEquals(PointsCode.TRANS_2004.getCode(), exception.getPointsCode().getCode());

    }

    @Test
    void consumePoints_sys_trans_no_exists() {
        PointsConsumeInput input = getPointsConsumeInput();
        PointsTypeDTO pointsType = getPointsTypeDTO(input);

        when(pointsTypeRepository.queryPointsTypeByInst(eq(input.getPoints().getPointsTypeNo()), eq(input.getCustomerInfo().getInstitutionId()))).thenReturn(pointsType);
        when(pointsTransRepository.existsPointsBySysTransNo(eq(input.getSysTransNo()))).thenReturn(new PointsTransDTO());
        when(merchantRepository.queryMerchantByMerchantNo(any(),any())).thenReturn(new MerchantDTO(){{
            setStatus(MerchantStatus.AVAILABLE.getStatus());
        }});
        when(cacheRepository.lock(any(String.class), any(Long.class), any(TimeUnit.class))).thenReturn(true);
        ArgumentCaptor<PointsTransDTO> argumentCaptor = ArgumentCaptor.forClass(PointsTransDTO.class);
        doNothing().when(pointsTransRepository).processTrans(argumentCaptor.capture(), any());

        PointsException exception = assertThrows(PointsException.class, () -> transactionService.consumePoints(input));
        assertEquals(PointsCode.TRANS_2003.getCode(), exception.getPointsCode().getCode());

        PointsTransDTO pointsTransDTO = argumentCaptor.getValue();
        assertNotNull(pointsTransDTO);
        assertAll(
                () -> assertEquals(TransStatus.FAILED.getCode(), pointsTransDTO.getTransStatus()),
                () -> assertNotNull(pointsTransDTO.getTransNo()),
                () -> assertEquals(input.getCustomerInfo().getCustomerId(), pointsTransDTO.getCustomerId()),
                () -> assertEquals(input.getPoints().getPointsTypeNo(), pointsTransDTO.getPointsTypeNo()),
                () -> assertEquals(input.getCustomerInfo().getInstitutionId(), pointsTransDTO.getInstitutionId()),
                () -> assertNotNull(pointsTransDTO.getTransDate()),
                () -> assertNotNull(pointsTransDTO.getTransTime()),
                () -> assertEquals(TransType.CONSUME_POINT.getCode(), pointsTransDTO.getTransTypeNo()),
                () -> assertEquals(DebitOrCredit.DEBIT.getCode(), pointsTransDTO.getDebitOrCredit()),
                () -> assertEquals(input.getPoints().getPointsAmount(), pointsTransDTO.getPointsAmount()),
                () -> assertEquals(ReversedFlag.NOT_REVERSED.getCode(), pointsTransDTO.getReversedFlag()),
                () -> assertEquals(Constants.DEFAULT_SYSTEM_TRANS_CHANNEL, pointsTransDTO.getTransChannel()),
                () -> assertEquals(input.getMerchantNo(), pointsTransDTO.getMerchantNo()),
                () -> assertEquals(input.getVoucher().getVoucherType().getType(), pointsTransDTO.getVoucherTypeNo()),
                () -> assertEquals(Constants.DEFAULT_OPERATOR_SYSTEM, pointsTransDTO.getOperator()),
                () -> assertEquals(input.getSysTransNo(), pointsTransDTO.getSysTransNo()),
                () -> assertEquals(input.getPoints().getPointsAmount().divide(pointsType.getRate(), 2, RoundingMode.HALF_EVEN), pointsTransDTO.getClearingAmt()),
                () -> assertNotNull(pointsTransDTO.getDescription()),
                () -> assertNotNull(pointsTransDTO.getTransTimestamp())
        );
    }

    private PointsTypeDTO getPointsTypeDTO(CustomerInfoBase input) {
        PointsTypeDTO pointsType = new PointsTypeDTO();
        pointsType.setPointsTypeNo("points_type_no");
        pointsType.setPointsTypeName("points_type_name");
        pointsType.setDescription("description");
        pointsType.setInstitutionId(input.getCustomerInfo().getInstitutionId());
        pointsType.setRate(new BigDecimal(5));
        return pointsType;
    }

    @Test
    void consumePoints_customerId_points_balance_not_enough() {
        PointsConsumeInput input = getPointsConsumeInput();
        PointsTypeDTO pointsType = getPointsTypeDTO(input);
        PointsAccountInfoDTO pointsAccountInfo = new PointsAccountInfoDTO();
        pointsAccountInfo.setPointsBalance(input.getPoints().getPointsAmount());
        pointsAccountInfo.setFreezingPoints(BigDecimal.ONE);
        pointsAccountInfo.setPointsAccountStatus(AccountStatus.AVAILABLE.getStatus());

        when(pointsTypeRepository.queryPointsTypeByInst(eq(input.getPoints().getPointsTypeNo()), eq(input.getCustomerInfo().getInstitutionId()))).thenReturn(pointsType);
        when(pointsTransRepository.existsPointsBySysTransNo(eq(input.getSysTransNo()))).thenReturn(null);
        when(pointsAccountInfoRepository.queryPointsAccountInfoByType(eq(input.getCustomerInfo().getCustomerId()), eq(pointsType.getPointsTypeNo()))).thenReturn(pointsAccountInfo);
        when(merchantRepository.queryMerchantByMerchantNo(any(),any())).thenReturn(new MerchantDTO(){{
            setStatus(MerchantStatus.AVAILABLE.getStatus());
        }});
        when(cacheRepository.lock(any(String.class), any(Long.class), any(TimeUnit.class))).thenReturn(true);

        ArgumentCaptor<PointsTransDTO> argumentCaptor = ArgumentCaptor.forClass(PointsTransDTO.class);
        doNothing().when(pointsTransRepository).processTrans(argumentCaptor.capture(), any());

        PointsException exception = assertThrows(PointsException.class, () -> transactionService.consumePoints(input));
        assertEquals(PointsCode.TRANS_2010.getCode(), exception.getPointsCode().getCode());
        PointsTransDTO pointsTransDTO = argumentCaptor.getValue();
        assertNotNull(pointsTransDTO);

        assertAll(
                () -> assertEquals(TransStatus.FAILED.getCode(), pointsTransDTO.getTransStatus()),
                () -> assertNotNull(pointsTransDTO.getTransNo()),
                () -> assertEquals(input.getCustomerInfo().getCustomerId(), pointsTransDTO.getCustomerId()),
                () -> assertEquals(input.getPoints().getPointsTypeNo(), pointsTransDTO.getPointsTypeNo()),
                () -> assertEquals(input.getCustomerInfo().getInstitutionId(), pointsTransDTO.getInstitutionId()),
                () -> assertNotNull(pointsTransDTO.getTransDate()),
                () -> assertNotNull(pointsTransDTO.getTransTime()),
                () -> assertEquals(TransType.CONSUME_POINT.getCode(), pointsTransDTO.getTransTypeNo()),
                () -> assertEquals(DebitOrCredit.DEBIT.getCode(), pointsTransDTO.getDebitOrCredit()),
                () -> assertEquals(input.getPoints().getPointsAmount(), pointsTransDTO.getPointsAmount()),
                () -> assertEquals(ReversedFlag.NOT_REVERSED.getCode(), pointsTransDTO.getReversedFlag()),
                () -> assertEquals(Constants.DEFAULT_SYSTEM_TRANS_CHANNEL, pointsTransDTO.getTransChannel()),
                () -> assertEquals(input.getMerchantNo(), pointsTransDTO.getMerchantNo()),
                () -> assertEquals(input.getVoucher().getVoucherType().getType(), pointsTransDTO.getVoucherTypeNo()),
                () -> assertEquals(Constants.DEFAULT_OPERATOR_SYSTEM, pointsTransDTO.getOperator()),
                () -> assertEquals(input.getSysTransNo(), pointsTransDTO.getSysTransNo()),
                () -> assertEquals(input.getPoints().getPointsAmount().divide(pointsType.getRate(),2, RoundingMode.HALF_EVEN), pointsTransDTO.getClearingAmt()),
                () -> assertNotNull(pointsTransDTO.getDescription()),
                () -> assertNotNull(pointsTransDTO.getTransTimestamp())
        );
    }

    @Test
    void consumePoints_success() {
        PointsConsumeInput input = getPointsConsumeInput();
        PointsTypeDTO pointsType = getPointsTypeDTO(input);
        PointsAccountInfoDTO pointsAccountInfo = new PointsAccountInfoDTO();
        pointsAccountInfo.setPointsBalance(input.getPoints().getPointsAmount());
        pointsAccountInfo.setPointsAccountStatus(AccountStatus.AVAILABLE.getStatus());

        when(pointsTypeRepository.queryPointsTypeByInst(eq(input.getPoints().getPointsTypeNo()), eq(input.getCustomerInfo().getInstitutionId()))).thenReturn(pointsType);
        when(pointsTransRepository.existsPointsBySysTransNo(eq(input.getSysTransNo()))).thenReturn(null);
        when(pointsAccountInfoRepository.queryPointsAccountInfoByType(eq(input.getCustomerInfo().getCustomerId()), eq(pointsType.getPointsTypeNo()))).thenReturn(pointsAccountInfo);
        when(merchantRepository.queryMerchantByMerchantNo(any(),any())).thenReturn(new MerchantDTO(){{
            setStatus(MerchantStatus.AVAILABLE.getStatus());
        }});
        when(cacheRepository.lock(any(String.class), any(Long.class), any(TimeUnit.class))).thenReturn(true);

        ArgumentCaptor<PointsTransDTO> argumentCaptor = ArgumentCaptor.forClass(PointsTransDTO.class);
        doNothing().when(pointsTransRepository).processTrans(argumentCaptor.capture(), any());

        PointsConsumeOutput output = transactionService.consumePoints(input);
        assertNotNull(output);
        assertAll(
                () -> assertEquals(input.getSysTransNo(), output.getSysTransNo()),
                () -> assertNotNull(output.getPointsTransNo()),
                () -> assertNotNull(output.getPayTime())
        );

        PointsTransDTO pointsTransDTO = argumentCaptor.getValue();
        assertNotNull(pointsTransDTO);
        assertAll(
                () -> assertEquals(TransStatus.SUCCESS.getCode(), pointsTransDTO.getTransStatus()),
                () -> assertNotNull(pointsTransDTO.getTransNo()),
                () -> assertEquals(input.getCustomerInfo().getCustomerId(), pointsTransDTO.getCustomerId()),
                () -> assertEquals(input.getPoints().getPointsTypeNo(), pointsTransDTO.getPointsTypeNo()),
                () -> assertEquals(input.getCustomerInfo().getInstitutionId(), pointsTransDTO.getInstitutionId()),
                () -> assertNotNull(pointsTransDTO.getTransDate()),
                () -> assertNotNull(pointsTransDTO.getTransTime()),
                () -> assertEquals(TransType.CONSUME_POINT.getCode(), pointsTransDTO.getTransTypeNo()),
                () -> assertEquals(DebitOrCredit.DEBIT.getCode(), pointsTransDTO.getDebitOrCredit()),
                () -> assertEquals(input.getPoints().getPointsAmount(), pointsTransDTO.getPointsAmount()),
                () -> assertEquals(ReversedFlag.NOT_REVERSED.getCode(), pointsTransDTO.getReversedFlag()),
                () -> assertEquals(Constants.DEFAULT_SYSTEM_TRANS_CHANNEL, pointsTransDTO.getTransChannel()),
                () -> assertEquals(input.getMerchantNo(), pointsTransDTO.getMerchantNo()),
                () -> assertEquals(input.getVoucher().getVoucherType().getType(), pointsTransDTO.getVoucherTypeNo()),
                () -> assertEquals(Constants.DEFAULT_OPERATOR_SYSTEM, pointsTransDTO.getOperator()),
                () -> assertEquals(input.getSysTransNo(), pointsTransDTO.getSysTransNo()),
                () -> assertEquals(input.getPoints().getPointsAmount().divide(pointsType.getRate(),2, RoundingMode.HALF_EVEN), pointsTransDTO.getClearingAmt()),
                () -> assertNotNull(pointsTransDTO.getDescription()),
                () -> assertNotNull(pointsTransDTO.getTransTimestamp())
        );
    }

    private PointsConsumeInput getPointsConsumeInput() {
        String SysTransNo = String.valueOf(Sequence.getId());
        PointsConsumeInput input = new PointsConsumeInput();
        input.setSysTransNo(SysTransNo);
        input.setMerchantNo("123");
        input.setInstitutionNo("100000");
        input.setVoucher(new Voucher() {{
            setVoucherNo("844703788571820032");
            setVoucherType(VoucherType.CUST);
        }});
        input.setPoints(new Points() {{
            setPointsTypeNo("844700918006939648");
            setPointsAmount(new BigDecimal(101));
        }});
        input.setCustomerInfo(new CustomerInfoValidResult(
                new CustomerInfoValidResult.Institution() {{
                    setInstitutionId("institution_id");
                }},
                new CustomerInfoValidResult.Customer() {{
                    setCustomerId("customer_id");
                }}
        ));
        return input;
    }

    @Test
    void accTransPoints_points_type_not_exists() {
        PointsAccTransInput input = getPointsAccTransInput();

        when(pointsTypeRepository.queryPointsTypeByInst(eq(input.getPoints().getPointsTypeNo()), eq(input.getCustomerInfo().getInstitutionId()))).thenReturn(null);
        when(cacheRepository.lock(any(String.class), any(Long.class), any(TimeUnit.class))).thenReturn(true);

        PointsException exception = assertThrows(PointsException.class, () -> transactionService.accTransPoints(input));
        assertEquals(PointsCode.TRANS_2005.getCode(), exception.getPointsCode().getCode());
        verify(pointsTransRepository, never()).processTrans(any(PointsTransDTO.class), any(PointsTransDTO.class));
    }

    @Test
    void accTransPoints_sys_trans_no_exists() {
        PointsAccTransInput input = getPointsAccTransInput();
        input.setDebitOrCredit(DebitOrCredit.DEBIT);
        input.setTransType(TransType.CONSUME_POINT);
        PointsTypeDTO pointsType = getPointsTypeDTO(input);

        when(pointsTypeRepository.queryPointsTypeByInst(eq(input.getPoints().getPointsTypeNo()), eq(input.getCustomerInfo().getInstitutionId()))).thenReturn(pointsType);
        when(pointsTransRepository.existsPointsBySysTransNo(eq(input.getSysTransNo()))).thenReturn(new PointsTransDTO());
        when(cacheRepository.lock(any(String.class), any(Long.class), any(TimeUnit.class))).thenReturn(true);

        ArgumentCaptor<PointsTransDTO> argumentCaptor = ArgumentCaptor.forClass(PointsTransDTO.class);
        doNothing().when(pointsTransRepository).processTrans(argumentCaptor.capture(), any());

        PointsException exception = assertThrows(PointsException.class, () -> transactionService.accTransPoints(input));
        assertEquals(PointsCode.TRANS_2003.getCode(), exception.getPointsCode().getCode());

        PointsTransDTO pointsTransDTO = argumentCaptor.getValue();
        assertNotNull(pointsTransDTO);
        assertAll(
                () -> assertEquals(TransStatus.FAILED.getCode(), pointsTransDTO.getTransStatus()),
                () -> assertNotNull(pointsTransDTO.getTransNo()),
                () -> assertEquals(input.getCustomerInfo().getCustomerId(), pointsTransDTO.getCustomerId()),
                () -> assertEquals(input.getPoints().getPointsTypeNo(), pointsTransDTO.getPointsTypeNo()),
                () -> assertEquals(input.getCustomerInfo().getInstitutionId(), pointsTransDTO.getInstitutionId()),
                () -> assertNotNull(pointsTransDTO.getTransDate()),
                () -> assertNotNull(pointsTransDTO.getTransTime()),
                () -> assertEquals(input.getTransType().getCode(), pointsTransDTO.getTransTypeNo()),
                () -> assertEquals(input.getDebitOrCredit().getCode(), pointsTransDTO.getDebitOrCredit()),
                () -> assertEquals(input.getPoints().getPointsAmount(), pointsTransDTO.getPointsAmount()),
                () -> assertEquals(ReversedFlag.NOT_REVERSED.getCode(), pointsTransDTO.getReversedFlag()),
                () -> assertEquals(Constants.DEFAULT_SYSTEM_TRANS_CHANNEL, pointsTransDTO.getTransChannel()),
                () -> assertEquals(input.getMerchantNo(), pointsTransDTO.getMerchantNo()),
                () -> assertEquals(input.getVoucher().getVoucherType().getType(), pointsTransDTO.getVoucherTypeNo()),
                () -> assertEquals(Constants.DEFAULT_OPERATOR_SYSTEM, pointsTransDTO.getOperator()),
                () -> assertEquals(input.getSysTransNo(), pointsTransDTO.getSysTransNo()),
                () -> assertEquals(BigDecimal.ZERO, pointsTransDTO.getClearingAmt()),
                () -> assertNotNull(pointsTransDTO.getDescription()),
                () -> assertNotNull(pointsTransDTO.getTransTimestamp())
        );
    }

    @Test
    void accTransPoints_debit_customerId_points_balance_not_enough() {
        PointsAccTransInput input = getPointsAccTransInput();
        input.setDebitOrCredit(DebitOrCredit.DEBIT);
        input.setTransType(TransType.CONSUME_POINT);
        PointsTypeDTO pointsType = getPointsTypeDTO(input);
        PointsAccountInfoDTO pointsAccountInfo = new PointsAccountInfoDTO();
        pointsAccountInfo.setPointsBalance(input.getPoints().getPointsAmount());
        pointsAccountInfo.setFreezingPoints(BigDecimal.ONE);
        pointsAccountInfo.setPointsAccountStatus(AccountStatus.AVAILABLE.getStatus());

        when(pointsTypeRepository.queryPointsTypeByInst(eq(input.getPoints().getPointsTypeNo()), eq(input.getCustomerInfo().getInstitutionId()))).thenReturn(pointsType);
        when(pointsTransRepository.existsPointsBySysTransNo(eq(input.getSysTransNo()))).thenReturn(null);
        when(pointsAccountInfoRepository.queryPointsAccountInfoByType(eq(input.getCustomerInfo().getCustomerId()), eq(pointsType.getPointsTypeNo()))).thenReturn(pointsAccountInfo);
        when(cacheRepository.lock(any(String.class), any(Long.class), any(TimeUnit.class))).thenReturn(true);

        ArgumentCaptor<PointsTransDTO> argumentCaptor = ArgumentCaptor.forClass(PointsTransDTO.class);
        doNothing().when(pointsTransRepository).processTrans(argumentCaptor.capture(), any());

        PointsException exception = assertThrows(PointsException.class, () -> transactionService.accTransPoints(input));
        assertEquals(PointsCode.TRANS_2010.getCode(), exception.getPointsCode().getCode());
        PointsTransDTO pointsTransDTO = argumentCaptor.getValue();
        assertNotNull(pointsTransDTO);

        assertAll(
                () -> assertEquals(TransStatus.FAILED.getCode(), pointsTransDTO.getTransStatus()),
                () -> assertNotNull(pointsTransDTO.getTransNo()),
                () -> assertEquals(input.getCustomerInfo().getCustomerId(), pointsTransDTO.getCustomerId()),
                () -> assertEquals(input.getPoints().getPointsTypeNo(), pointsTransDTO.getPointsTypeNo()),
                () -> assertEquals(input.getCustomerInfo().getInstitutionId(), pointsTransDTO.getInstitutionId()),
                () -> assertNotNull(pointsTransDTO.getTransDate()),
                () -> assertNotNull(pointsTransDTO.getTransTime()),
                () -> assertEquals(input.getTransType().getCode(), pointsTransDTO.getTransTypeNo()),
                () -> assertEquals(input.getDebitOrCredit().getCode(), pointsTransDTO.getDebitOrCredit()),
                () -> assertEquals(input.getPoints().getPointsAmount(), pointsTransDTO.getPointsAmount()),
                () -> assertEquals(ReversedFlag.NOT_REVERSED.getCode(), pointsTransDTO.getReversedFlag()),
                () -> assertEquals(Constants.DEFAULT_SYSTEM_TRANS_CHANNEL, pointsTransDTO.getTransChannel()),
                () -> assertEquals(input.getMerchantNo(), pointsTransDTO.getMerchantNo()),
                () -> assertEquals(input.getVoucher().getVoucherType().getType(), pointsTransDTO.getVoucherTypeNo()),
                () -> assertEquals(Constants.DEFAULT_OPERATOR_SYSTEM, pointsTransDTO.getOperator()),
                () -> assertEquals(input.getSysTransNo(), pointsTransDTO.getSysTransNo()),
                () -> assertEquals(BigDecimal.ZERO, pointsTransDTO.getClearingAmt()),
                () -> assertNotNull(pointsTransDTO.getDescription()),
                () -> assertNotNull(pointsTransDTO.getTransTimestamp())
        );
    }

    @Test
    void accTransPoints_credit_costLine_invalid() {
        PointsAccTransInput input = getPointsAccTransInput();
        input.setDebitOrCredit(DebitOrCredit.CREDIT);
        input.setTransType(TransType.GAIN_ASSERT_POINT);
        PointsTypeDTO pointsType = getPointsTypeDTO(input);
        PointsAccountInfoDTO pointsAccountInfo = new PointsAccountInfoDTO();
        pointsAccountInfo.setPointsBalance(input.getPoints().getPointsAmount());
        pointsAccountInfo.setPointsAccountStatus(AccountStatus.AVAILABLE.getStatus());

        when(pointsTypeRepository.queryPointsTypeByInst(eq(input.getPoints().getPointsTypeNo()), eq(input.getCustomerInfo().getInstitutionId()))).thenReturn(pointsType);
        when(pointsTransRepository.existsPointsBySysTransNo(eq(input.getSysTransNo()))).thenReturn(null);
        when(pointsAccountInfoRepository.queryPointsAccountInfoByType(eq(input.getCustomerInfo().getCustomerId()), eq(pointsType.getPointsTypeNo()))).thenReturn(pointsAccountInfo);
        when(cacheRepository.lock(any(String.class), any(Long.class), any(TimeUnit.class))).thenReturn(true);

        PointsException exception = assertThrows(PointsException.class, () -> transactionService.accTransPoints(input));
        assertEquals(PointsCode.TRANS_0047.getCode(), exception.getPointsCode().getCode());

        verify(pointsTransRepository, never()).processTrans(any(PointsTransDTO.class), any(PointsTransDTO.class));
    }

    @Test
    void accTransPoints_credit_endDate_invalid() {
        PointsAccTransInput input = getPointsAccTransInput();
        input.setDebitOrCredit(DebitOrCredit.CREDIT);
        input.setTransType(TransType.GAIN_ASSERT_POINT);
        input.setCostLine("2");
        PointsTypeDTO pointsType = getPointsTypeDTO(input);
        PointsAccountInfoDTO pointsAccountInfo = new PointsAccountInfoDTO();
        pointsAccountInfo.setPointsBalance(input.getPoints().getPointsAmount());
        pointsAccountInfo.setPointsAccountStatus(AccountStatus.AVAILABLE.getStatus());

        when(pointsTypeRepository.queryPointsTypeByInst(eq(input.getPoints().getPointsTypeNo()), eq(input.getCustomerInfo().getInstitutionId()))).thenReturn(pointsType);
        when(pointsTransRepository.existsPointsBySysTransNo(eq(input.getSysTransNo()))).thenReturn(null);
        when(pointsAccountInfoRepository.queryPointsAccountInfoByType(eq(input.getCustomerInfo().getCustomerId()), eq(pointsType.getPointsTypeNo()))).thenReturn(pointsAccountInfo);
        when(pointsCostRepository.queryPointsCostById(eq(input.getCostLine()),eq(input.getCustomerInfo().getInstitutionId()))).thenReturn(new PointsCostDTO());
        when(cacheRepository.lock(any(String.class), any(Long.class), any(TimeUnit.class))).thenReturn(true);

        PointsException exception = assertThrows(PointsException.class, () -> transactionService.accTransPoints(input));
        assertEquals(PointsCode.TRANS_0047.getCode(), exception.getPointsCode().getCode());

        verify(pointsTransRepository, never()).processTrans(any(PointsTransDTO.class), any(PointsTransDTO.class));
    }

    @Test
    void accTransPoints_debit_merchant_invalid() {
        PointsAccTransInput input = getPointsAccTransInput();
        input.setDebitOrCredit(DebitOrCredit.DEBIT);
        input.setTransType(TransType.CONSUME_POINT);
        PointsTypeDTO pointsType = getPointsTypeDTO(input);
        PointsAccountInfoDTO pointsAccountInfo = new PointsAccountInfoDTO();
        pointsAccountInfo.setPointsBalance(input.getPoints().getPointsAmount());
        pointsAccountInfo.setPointsAccountStatus(AccountStatus.AVAILABLE.getStatus());

        when(pointsTypeRepository.queryPointsTypeByInst(eq(input.getPoints().getPointsTypeNo()), eq(input.getCustomerInfo().getInstitutionId()))).thenReturn(pointsType);
        when(pointsTransRepository.existsPointsBySysTransNo(eq(input.getSysTransNo()))).thenReturn(null);
        when(pointsAccountInfoRepository.queryPointsAccountInfoByType(eq(input.getCustomerInfo().getCustomerId()), eq(pointsType.getPointsTypeNo()))).thenReturn(pointsAccountInfo);
        when(cacheRepository.lock(any(String.class), any(Long.class), any(TimeUnit.class))).thenReturn(true);

        PointsException exception = assertThrows(PointsException.class, () -> transactionService.accTransPoints(input));
        assertEquals(PointsCode.TRANS_2004.getCode(), exception.getPointsCode().getCode());

    }

    @Test
    void accTransPoints_debit_success() {
        PointsAccTransInput input = getPointsAccTransInput();
        input.setDebitOrCredit(DebitOrCredit.DEBIT);
        input.setTransType(TransType.CONSUME_POINT);
        PointsTypeDTO pointsType = getPointsTypeDTO(input);
        PointsAccountInfoDTO pointsAccountInfo = new PointsAccountInfoDTO();
        pointsAccountInfo.setPointsBalance(input.getPoints().getPointsAmount());
        pointsAccountInfo.setPointsAccountStatus(AccountStatus.AVAILABLE.getStatus());

        when(pointsTypeRepository.queryPointsTypeByInst(eq(input.getPoints().getPointsTypeNo()), eq(input.getCustomerInfo().getInstitutionId()))).thenReturn(pointsType);
        when(pointsTransRepository.existsPointsBySysTransNo(eq(input.getSysTransNo()))).thenReturn(null);
        when(pointsAccountInfoRepository.queryPointsAccountInfoByType(eq(input.getCustomerInfo().getCustomerId()), eq(pointsType.getPointsTypeNo()))).thenReturn(pointsAccountInfo);
        when(merchantRepository.queryMerchantByMerchantNo(any(),any())).thenReturn(new MerchantDTO(){{
            setStatus(MerchantStatus.AVAILABLE.getStatus());
        }});

        when(cacheRepository.lock(any(String.class), any(Long.class), any(TimeUnit.class))).thenReturn(true);

        ArgumentCaptor<PointsTransDTO> argumentCaptor = ArgumentCaptor.forClass(PointsTransDTO.class);
        doNothing().when(pointsTransRepository).processTrans(argumentCaptor.capture(), any());

        PointsAccTransOutput output = transactionService.accTransPoints(input);
        assertNotNull(output);
        assertAll(
                () -> assertEquals(pointsAccountInfo.getPointsBalance(), output.getPointsBalance()),
                () -> assertNotNull(output.getTransDate()),
                () -> assertNotNull(output.getTransNo())
        );

        PointsTransDTO pointsTransDTO = argumentCaptor.getValue();
        assertNotNull(pointsTransDTO);

        assertAll(
                () -> assertEquals(TransStatus.SUCCESS.getCode(), pointsTransDTO.getTransStatus()),
                () -> assertNotNull(pointsTransDTO.getTransNo()),
                () -> assertEquals(input.getCustomerInfo().getCustomerId(), pointsTransDTO.getCustomerId()),
                () -> assertEquals(input.getPoints().getPointsTypeNo(), pointsTransDTO.getPointsTypeNo()),
                () -> assertEquals(input.getCustomerInfo().getInstitutionId(), pointsTransDTO.getInstitutionId()),
                () -> assertNotNull(pointsTransDTO.getTransDate()),
                () -> assertNotNull(pointsTransDTO.getTransTime()),
                () -> assertEquals(input.getTransType().getCode(), pointsTransDTO.getTransTypeNo()),
                () -> assertEquals(input.getDebitOrCredit().getCode(), pointsTransDTO.getDebitOrCredit()),
                () -> assertEquals(input.getPoints().getPointsAmount(), pointsTransDTO.getPointsAmount()),
                () -> assertEquals(ReversedFlag.NOT_REVERSED.getCode(), pointsTransDTO.getReversedFlag()),
                () -> assertEquals(Constants.DEFAULT_SYSTEM_TRANS_CHANNEL, pointsTransDTO.getTransChannel()),
                () -> assertEquals(input.getMerchantNo(), pointsTransDTO.getMerchantNo()),
                () -> assertEquals(input.getVoucher().getVoucherType().getType(), pointsTransDTO.getVoucherTypeNo()),
                () -> assertEquals(Constants.DEFAULT_OPERATOR_SYSTEM, pointsTransDTO.getOperator()),
                () -> assertEquals(input.getSysTransNo(), pointsTransDTO.getSysTransNo()),
                () -> assertEquals(BigDecimal.ZERO, pointsTransDTO.getClearingAmt()),
                () -> assertNotNull(pointsTransDTO.getDescription()),
                () -> assertNotNull(pointsTransDTO.getTransTimestamp())
        );
    }

    @Test
    void accTransPoints_credit_success() {
        PointsAccTransInput input = getPointsAccTransInput();
        input.setDebitOrCredit(DebitOrCredit.CREDIT);
        input.setTransType(TransType.GAIN_ASSERT_POINT);
        input.setCostLine("2");
        input.getPoints().setEndDate(new SimpleDateFormat(Constants.DATE_TIME_FORMATTER_YYYYMMDD_HHMMSS).format(new Date(System.currentTimeMillis() + 100000000L)));
        PointsTypeDTO pointsType = getPointsTypeDTO(input);
        PointsAccountInfoDTO pointsAccountInfo = new PointsAccountInfoDTO();
        pointsAccountInfo.setPointsBalance(input.getPoints().getPointsAmount());
        pointsAccountInfo.setPointsAccountStatus(AccountStatus.AVAILABLE.getStatus());

        when(pointsTypeRepository.queryPointsTypeByInst(eq(input.getPoints().getPointsTypeNo()), eq(input.getCustomerInfo().getInstitutionId()))).thenReturn(pointsType);
        when(pointsTransRepository.existsPointsBySysTransNo(eq(input.getSysTransNo()))).thenReturn(null);
        when(pointsAccountInfoRepository.queryPointsAccountInfoByType(eq(input.getCustomerInfo().getCustomerId()), eq(pointsType.getPointsTypeNo()))).thenReturn(pointsAccountInfo);
        when(pointsCostRepository.queryPointsCostById(eq(input.getCostLine()),eq(input.getCustomerInfo().getInstitutionId()))).thenReturn(new PointsCostDTO());
        when(cacheRepository.lock(any(String.class), any(Long.class), any(TimeUnit.class))).thenReturn(true);

        ArgumentCaptor<PointsTransDTO> argumentCaptor = ArgumentCaptor.forClass(PointsTransDTO.class);
        doNothing().when(pointsTransRepository).processTrans(argumentCaptor.capture(), any());

        PointsAccTransOutput output = transactionService.accTransPoints(input);
        assertNotNull(output);
        assertAll(
                () -> assertEquals(pointsAccountInfo.getPointsBalance(), output.getPointsBalance()),
                () -> assertNotNull(output.getTransDate()),
                () -> assertNotNull(output.getTransNo())
        );

        PointsTransDTO pointsTransDTO = argumentCaptor.getValue();
        assertNotNull(pointsTransDTO);

        assertAll(
                () -> assertEquals(TransStatus.SUCCESS.getCode(), pointsTransDTO.getTransStatus()),
                () -> assertNotNull(pointsTransDTO.getTransNo()),
                () -> assertEquals(input.getCustomerInfo().getCustomerId(), pointsTransDTO.getCustomerId()),
                () -> assertEquals(input.getPoints().getPointsTypeNo(), pointsTransDTO.getPointsTypeNo()),
                () -> assertEquals(input.getCustomerInfo().getInstitutionId(), pointsTransDTO.getInstitutionId()),
                () -> assertNotNull(pointsTransDTO.getTransDate()),
                () -> assertNotNull(pointsTransDTO.getTransTime()),
                () -> assertEquals(input.getTransType().getCode(), pointsTransDTO.getTransTypeNo()),
                () -> assertEquals(input.getDebitOrCredit().getCode(), pointsTransDTO.getDebitOrCredit()),
                () -> assertEquals(input.getPoints().getPointsAmount(), pointsTransDTO.getPointsAmount()),
                () -> assertEquals(ReversedFlag.NOT_REVERSED.getCode(), pointsTransDTO.getReversedFlag()),
                () -> assertEquals(Constants.DEFAULT_SYSTEM_TRANS_CHANNEL, pointsTransDTO.getTransChannel()),
                () -> assertNull(pointsTransDTO.getMerchantNo()),
                () -> assertEquals(input.getVoucher().getVoucherType().getType(), pointsTransDTO.getVoucherTypeNo()),
                () -> assertEquals(Constants.DEFAULT_OPERATOR_SYSTEM, pointsTransDTO.getOperator()),
                () -> assertEquals(input.getSysTransNo(), pointsTransDTO.getSysTransNo()),
                () -> assertEquals(BigDecimal.ZERO, pointsTransDTO.getClearingAmt()),
                () -> assertNotNull(pointsTransDTO.getDescription()),
                () -> assertNotNull(pointsTransDTO.getTransTimestamp()),
                () -> assertEquals(input.getCostLine(), pointsTransDTO.getCostLine()),
                () -> assertEquals(input.getPoints().getEndDate(), new SimpleDateFormat(Constants.DATE_TIME_FORMATTER_YYYYMMDD_HHMMSS).format(pointsTransDTO.getEndDate()))
        );
    }

    private PointsAccTransInput getPointsAccTransInput() {
        String SysTransNo = String.valueOf(Sequence.getId());

        PointsAccTransInput input = new PointsAccTransInput();
        input.setSysTransNo(SysTransNo);
        input.setMerchantNo("123");
        input.setInstitutionNo("100000");
        input.setVoucher(new Voucher() {{
            setVoucherNo("844703788571820032");
            setVoucherType(VoucherType.CUST);
        }});
        input.setPoints(new Points() {{
            setPointsTypeNo("844700918006939648");
            setPointsAmount(new BigDecimal(101));
        }});
        input.setCustomerInfo(new CustomerInfoValidResult(
                new CustomerInfoValidResult.Institution() {{
                    setInstitutionId("institution_id");
                }},
                new CustomerInfoValidResult.Customer() {{
                    setCustomerId("customer_id");
                }}
        ));
        return input;
    }

    @Test
    void queryState_not_exists() {
        PointsQueryStateInput input = new PointsQueryStateInput() {{
            setSysTransNo("sys_trans_no_not_exists");
        }};
        when(pointsTransRepository.existsPointsBySysTransNo(eq(input.getSysTransNo()))).thenReturn(null);
        PointsException exception = assertThrows(PointsException.class, () -> transactionService.queryState(input));
        assertEquals(PointsCode.TRANS_2008.getCode(), exception.getPointsCode().getCode());
    }

    @Test
    void queryState_exists() {
        PointsQueryStateInput input = new PointsQueryStateInput() {{
            setSysTransNo("sys_trans_no_exists");
        }};
        PointsTransDTO pointsTrans = new PointsTransDTO();
        pointsTrans.setTransTimestamp(System.currentTimeMillis());
        pointsTrans.setTransNo(Sequence.getTransNo());
        pointsTrans.setTransStatus(TransStatus.SUCCESS.getCode());
        when(pointsTransRepository.existsPointsBySysTransNo(eq(input.getSysTransNo()))).thenReturn(pointsTrans);
        PointsQueryStateOutput output = transactionService.queryState(input);
        assertNotNull(output);
        assertAll(
                () -> assertEquals(pointsTrans.getTransNo(), output.getTransNo()),
                () -> assertNotNull(output.getTransDate()),
                () -> assertEquals(pointsTrans.getTransStatus(), output.getTransStatus().getCode())
        );
    }

    @Test
    void reversePoints_inst_not_exists() {
        PointsReverseInput input = new PointsReverseInput();

        when(institutionRepository.queryTopInstitution(eq(input.getInstitutionNo()))).thenReturn(null);
        when(cacheRepository.lock(any(String.class), any(Long.class), any(TimeUnit.class))).thenReturn(true);

        PointsException exception = assertThrows(PointsException.class, () -> transactionService.reversePoints(input));
        assertEquals(PointsCode.TRANS_1101.getCode(), exception.getPointsCode().getCode());
        verify(pointsTransRepository, never()).processTrans(any(PointsTransDTO.class), any(PointsTransDTO.class));
    }

    @Test
    void reversePoints_sys_trans_no_not_exists() {
        PointsReverseInput input = new PointsReverseInput();
        InstitutionDTO institutionDTO = getInstitutionDTO(input);

        when(institutionRepository.queryTopInstitution(eq(input.getInstitutionNo()))).thenReturn(institutionDTO);
        when(pointsTransRepository.queryPointsBySysTransNoAndStatus(eq(input.getReverseSysTransNo()), eq(TransStatus.SUCCESS.getCode()))).thenReturn(new ArrayList<>());
        when(cacheRepository.lock(any(String.class), any(Long.class), any(TimeUnit.class))).thenReturn(true);

        PointsException exception = assertThrows(PointsException.class, () -> transactionService.reversePoints(input));
        assertEquals(PointsCode.TRANS_2008.getCode(), exception.getPointsCode().getCode());
        verify(pointsTransRepository, never()).processTrans(any(PointsTransDTO.class), any(PointsTransDTO.class));
    }

    @Test
    void reversePoints_sys_trans_no_first_is_reversed() {
        PointsReverseInput input = new PointsReverseInput();
        input.setSysTransNo("sys_trans_no");
        input.setReverseSysTransNo("reverse_sys_trans_no");
        input.setInstitutionNo("inst_no");
        input.setOperator("input_operator");
        InstitutionDTO institutionDTO = getInstitutionDTO(input);
        PointsTransDTO pointsTransDTO = new PointsTransDTO();
        pointsTransDTO.setReversedFlag(ReversedFlag.REVERSED.getCode());
        pointsTransDTO.setSysTransNo(input.getSysTransNo());
        pointsTransDTO.setTransStatus(TransStatus.SUCCESS.getCode());
        pointsTransDTO.setTransNo(Sequence.getTransNo());
        pointsTransDTO.setOperator("operator");
        pointsTransDTO.setInstitutionId(institutionDTO.getInstitutionId());

        when(institutionRepository.queryTopInstitution(eq(input.getInstitutionNo()))).thenReturn(institutionDTO);
        when(pointsTransRepository.queryPointsBySysTransNoAndStatus(eq(input.getReverseSysTransNo()), eq(TransStatus.SUCCESS.getCode()))).thenReturn(new ArrayList<>(){{
            add(pointsTransDTO);
        }});
        when(cacheRepository.lock(any(String.class), any(Long.class), any(TimeUnit.class))).thenReturn(true);
        ArgumentCaptor<PointsTransDTO> argumentCaptor = ArgumentCaptor.forClass(PointsTransDTO.class);
        doNothing().when(pointsTransRepository).processTrans(argumentCaptor.capture(), any());

        PointsException exception = assertThrows(PointsException.class, () -> transactionService.reversePoints(input));
        assertEquals(PointsCode.TRANS_2012.getCode(), exception.getPointsCode().getCode());

        PointsTransDTO pointsTrans = argumentCaptor.getValue();
        assertNotNull(pointsTrans);

        assertAll(
                () -> assertEquals(TransStatus.FAILED.getCode(), pointsTrans.getTransStatus()),
                () -> assertNotEquals(pointsTransDTO.getTransNo(),pointsTrans.getTransNo()),
                () -> assertNotNull(pointsTrans.getTransNo()),
                () -> assertEquals(ReversedFlag.REVERSED.getCode(), pointsTrans.getReversedFlag()),
                () -> assertEquals(input.getSysTransNo(), pointsTrans.getSysTransNo()),
                () -> assertEquals(input.getOperator(), pointsTrans.getOperator()),
                () -> assertNotNull(pointsTrans.getDescription())
        );
    }


    @Test
    void reversePoints_sys_trans_no_exists_reversed() {
        PointsReverseInput input = new PointsReverseInput();
        input.setSysTransNo("sys_trans_no");
        input.setReverseSysTransNo("reverse_sys_trans_no");
        input.setInstitutionNo("inst_no");
        input.setOperator("input_operator");
        InstitutionDTO institutionDTO = getInstitutionDTO(input);

        PointsTransDTO pointsTransFirst = new PointsTransDTO();
        pointsTransFirst.setId(1L);
        pointsTransFirst.setReversedFlag(ReversedFlag.NOT_REVERSED.getCode());
        pointsTransFirst.setSysTransNo(input.getSysTransNo());
        pointsTransFirst.setTransStatus(TransStatus.SUCCESS.getCode());
        pointsTransFirst.setTransNo(Sequence.getTransNo());
        pointsTransFirst.setTransTimestamp(System.currentTimeMillis());
        pointsTransFirst.setOperator("first_operator");
        pointsTransFirst.setInstitutionId(institutionDTO.getInstitutionId());

        PointsTransDTO pointsTransSecond = new PointsTransDTO();
        pointsTransSecond.setId(2L);
        pointsTransSecond.setReversedFlag(ReversedFlag.REVERSED.getCode());
        pointsTransSecond.setSysTransNo(input.getSysTransNo());
        pointsTransSecond.setTransStatus(TransStatus.SUCCESS.getCode());
        pointsTransSecond.setTransNo(Sequence.getTransNo());
        pointsTransFirst.setTransTimestamp(System.currentTimeMillis());
        pointsTransSecond.setOperator("second_operator");
        pointsTransSecond.setInstitutionId(institutionDTO.getInstitutionId());

        when(institutionRepository.queryTopInstitution(eq(input.getInstitutionNo()))).thenReturn(institutionDTO);
        when(pointsTransRepository.queryPointsBySysTransNoAndStatus(eq(input.getReverseSysTransNo()), eq(TransStatus.SUCCESS.getCode()))).thenReturn(new ArrayList<>(){{
            add(pointsTransFirst);
        }});
        when(pointsTransRepository.queryPointsByOldTransNoAndStatus(eq(pointsTransFirst.getTransNo()), eq(TransStatus.SUCCESS.getCode()))).thenReturn(new ArrayList<>(){{
            add(pointsTransSecond);
        }});
        when(cacheRepository.lock(any(String.class), any(Long.class), any(TimeUnit.class))).thenReturn(true);

        ArgumentCaptor<PointsTransDTO> argumentCaptor = ArgumentCaptor.forClass(PointsTransDTO.class);
        doNothing().when(pointsTransRepository).processTrans(argumentCaptor.capture(), any());

        PointsException exception = assertThrows(PointsException.class, () -> transactionService.reversePoints(input));
        assertEquals(PointsCode.TRANS_2011.getCode(), exception.getPointsCode().getCode());

        PointsTransDTO pointsTrans = argumentCaptor.getValue();
        assertNotNull(pointsTrans);

        assertAll(
                () -> assertEquals(TransStatus.FAILED.getCode(), pointsTrans.getTransStatus()),
                () -> assertNotEquals(pointsTransFirst.getTransNo(),pointsTrans.getTransNo()),
                () -> assertNotEquals(pointsTransSecond.getTransNo(),pointsTrans.getTransNo()),
                () -> assertNotNull(pointsTrans.getTransNo()),
                () -> assertEquals(ReversedFlag.REVERSED.getCode(), pointsTrans.getReversedFlag()),
                () -> assertEquals(input.getSysTransNo(), pointsTrans.getSysTransNo()),
                () -> assertEquals(input.getOperator(), pointsTrans.getOperator()),
                () -> assertNotNull(pointsTrans.getDescription())
        );
    }

    @Test
    void reversePoints_sys_trans_no_double() {
        PointsReverseInput input = new PointsReverseInput();
        input.setSysTransNo("sys_trans_no");
        input.setReverseSysTransNo("reverse_sys_trans_no");
        input.setInstitutionNo("inst_no");
        input.setOperator("input_operator");
        InstitutionDTO institutionDTO = getInstitutionDTO(input);

        PointsTransDTO pointsTransFirst = new PointsTransDTO();
        pointsTransFirst.setId(1L);
        pointsTransFirst.setReversedFlag(ReversedFlag.NOT_REVERSED.getCode());
        pointsTransFirst.setSysTransNo(input.getSysTransNo());
        pointsTransFirst.setTransStatus(TransStatus.SUCCESS.getCode());
        pointsTransFirst.setTransNo(Sequence.getTransNo());
        pointsTransFirst.setOperator("first_operator");
        pointsTransFirst.setInstitutionId(institutionDTO.getInstitutionId());

        PointsTransDTO pointsTransSecond = new PointsTransDTO();
        pointsTransSecond.setId(2L);
        pointsTransSecond.setReversedFlag(ReversedFlag.NOT_REVERSED.getCode());
        pointsTransSecond.setSysTransNo(input.getSysTransNo());
        pointsTransSecond.setTransStatus(TransStatus.SUCCESS.getCode());
        pointsTransSecond.setTransNo(Sequence.getTransNo());
        pointsTransSecond.setOperator("second_operator");
        pointsTransSecond.setInstitutionId(institutionDTO.getInstitutionId());

        when(institutionRepository.queryTopInstitution(eq(input.getInstitutionNo()))).thenReturn(institutionDTO);
        when(pointsTransRepository.queryPointsBySysTransNoAndStatus(eq(input.getReverseSysTransNo()), eq(TransStatus.SUCCESS.getCode()))).thenReturn(new ArrayList<>(){{
            add(pointsTransFirst);
            add(pointsTransSecond);
        }});
        when(cacheRepository.lock(any(String.class), any(Long.class), any(TimeUnit.class))).thenReturn(true);

        ArgumentCaptor<PointsTransDTO> argumentCaptor = ArgumentCaptor.forClass(PointsTransDTO.class);
        doNothing().when(pointsTransRepository).processTrans(argumentCaptor.capture(), any());

        PointsException exception = assertThrows(PointsException.class, () -> transactionService.reversePoints(input));
        assertEquals(PointsCode.TRANS_2016.getCode(), exception.getPointsCode().getCode());

        PointsTransDTO pointsTrans = argumentCaptor.getValue();
        assertNotNull(pointsTrans);

        assertAll(
                () -> assertEquals(TransStatus.FAILED.getCode(), pointsTrans.getTransStatus()),
                () -> assertNotEquals(pointsTransFirst.getTransNo(),pointsTrans.getTransNo()),
                () -> assertNotEquals(pointsTransSecond.getTransNo(),pointsTrans.getTransNo()),
                () -> assertNotNull(pointsTrans.getTransNo()),
                () -> assertEquals(ReversedFlag.REVERSED.getCode(), pointsTrans.getReversedFlag()),
                () -> assertEquals(input.getSysTransNo(), pointsTrans.getSysTransNo()),
                () -> assertEquals(input.getOperator(), pointsTrans.getOperator()),
                () -> assertNotNull(pointsTrans.getDescription())
        );
    }

    @Test
    void reversePoints_sys_trans_no_not_same_month() throws ParseException {
        PointsReverseInput input = new PointsReverseInput();
        input.setSysTransNo("sys_trans_no");
        input.setReverseSysTransNo("reverse_sys_trans_no");
        input.setInstitutionNo("inst_no");
        input.setOperator("input_operator");
        InstitutionDTO institutionDTO = getInstitutionDTO(input);

        PointsTransDTO pointsTransDTO = new PointsTransDTO();
        pointsTransDTO.setId(1L);
        pointsTransDTO.setReversedFlag(ReversedFlag.NOT_REVERSED.getCode());
        pointsTransDTO.setSysTransNo(input.getSysTransNo());
        pointsTransDTO.setTransStatus(TransStatus.SUCCESS.getCode());
        pointsTransDTO.setTransNo(Sequence.getTransNo());
        pointsTransDTO.setOperator("first_operator");
        pointsTransDTO.setInstitutionId(institutionDTO.getInstitutionId());
        pointsTransDTO.setTransDate("20200101");
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        pointsTransDTO.setTransTimestamp(format.parse(pointsTransDTO.getTransDate()).getTime());

        when(institutionRepository.queryTopInstitution(eq(input.getInstitutionNo()))).thenReturn(institutionDTO);
        when(pointsTransRepository.queryPointsBySysTransNoAndStatus(eq(input.getReverseSysTransNo()), eq(TransStatus.SUCCESS.getCode()))).thenReturn(new ArrayList<>(){{
            add(pointsTransDTO);
        }});
        when(cacheRepository.lock(any(String.class), any(Long.class), any(TimeUnit.class))).thenReturn(true);

        ArgumentCaptor<PointsTransDTO> argumentCaptor = ArgumentCaptor.forClass(PointsTransDTO.class);
        doNothing().when(pointsTransRepository).processTrans(argumentCaptor.capture(), any());

        PointsException exception = assertThrows(PointsException.class, () -> transactionService.reversePoints(input));
        assertEquals(PointsCode.TRANS_2013.getCode(), exception.getPointsCode().getCode());

        PointsTransDTO pointsTrans = argumentCaptor.getValue();
        assertNotNull(pointsTrans);

        assertAll(
                () -> assertEquals(TransStatus.FAILED.getCode(), pointsTrans.getTransStatus()),
                () -> assertNotEquals(pointsTransDTO.getTransNo(),pointsTrans.getTransNo()),
                () -> assertNotNull(pointsTrans.getTransNo()),
                () -> assertEquals(ReversedFlag.REVERSED.getCode(), pointsTrans.getReversedFlag()),
                () -> assertEquals(input.getSysTransNo(), pointsTrans.getSysTransNo()),
                () -> assertEquals(input.getOperator(), pointsTrans.getOperator()),
                () -> assertNotNull(pointsTrans.getDescription())
        );
    }


    @Test
    void reversePoints_sys_trans_no_exists_accountingTrans() {
        PointsReverseInput input = new PointsReverseInput();
        input.setSysTransNo("sys_trans_no");
        input.setReverseSysTransNo("reverse_sys_trans_no");
        input.setInstitutionNo("inst_no");
        input.setOperator("input_operator");
        InstitutionDTO institutionDTO = getInstitutionDTO(input);

        PointsTransDTO pointsTransDTO = new PointsTransDTO();
        pointsTransDTO.setId(1L);
        pointsTransDTO.setReversedFlag(ReversedFlag.NOT_REVERSED.getCode());
        pointsTransDTO.setSysTransNo(input.getSysTransNo());
        pointsTransDTO.setTransStatus(TransStatus.SUCCESS.getCode());
        pointsTransDTO.setTransNo(Sequence.getTransNo());
        pointsTransDTO.setOperator("first_operator");
        pointsTransDTO.setInstitutionId(institutionDTO.getInstitutionId());
        pointsTransDTO.setTransDate(LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE));
        pointsTransDTO.setTransTimestamp(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());

        when(institutionRepository.queryTopInstitution(eq(input.getInstitutionNo()))).thenReturn(institutionDTO);
        when(pointsTransRepository.queryPointsBySysTransNoAndStatus(eq(input.getReverseSysTransNo()), eq(TransStatus.SUCCESS.getCode()))).thenReturn(new ArrayList<>(){{
            add(pointsTransDTO);
        }});
        when(pointsTransRepository.existsAccountingTrans(eq(pointsTransDTO.getCustomerId()), eq(pointsTransDTO.getPointsTypeNo()), eq(pointsTransDTO.getTransNo()), anyBoolean())).thenReturn(true);
        when(cacheRepository.lock(any(String.class), any(Long.class), any(TimeUnit.class))).thenReturn(true);

        ArgumentCaptor<PointsTransDTO> argumentCaptor = ArgumentCaptor.forClass(PointsTransDTO.class);
        doNothing().when(pointsTransRepository).processTrans(argumentCaptor.capture(), any());

        PointsException exception = assertThrows(PointsException.class, () -> transactionService.reversePoints(input));
        assertEquals(PointsCode.TRANS_2014.getCode(), exception.getPointsCode().getCode());

        PointsTransDTO pointsTrans = argumentCaptor.getValue();
        assertNotNull(pointsTrans);

        assertAll(
                () -> assertEquals(TransStatus.FAILED.getCode(), pointsTrans.getTransStatus()),
                () -> assertNotEquals(pointsTransDTO.getTransNo(),pointsTrans.getTransNo()),
                () -> assertNotNull(pointsTrans.getTransNo()),
                () -> assertEquals(ReversedFlag.REVERSED.getCode(), pointsTrans.getReversedFlag()),
                () -> assertEquals(input.getSysTransNo(), pointsTrans.getSysTransNo()),
                () -> assertEquals(input.getOperator(), pointsTrans.getOperator()),
                () -> assertNotNull(pointsTrans.getDescription())
        );
    }

    @Test
    void reversePoints_sys_trans_no_when_not_same_day_and_credit_has_used() {
        PointsReverseInput input = new PointsReverseInput();
        input.setSysTransNo("sys_trans_no");
        input.setReverseSysTransNo("reverse_sys_trans_no");
        input.setInstitutionNo("inst_no");
        input.setOperator("input_operator");
        InstitutionDTO institutionDTO = getInstitutionDTO(input);

        PointsTransDTO pointsTransDTO = new PointsTransDTO();
        pointsTransDTO.setId(1L);
        pointsTransDTO.setReversedFlag(ReversedFlag.NOT_REVERSED.getCode());
        pointsTransDTO.setSysTransNo(input.getSysTransNo());
        pointsTransDTO.setTransStatus(TransStatus.SUCCESS.getCode());
        pointsTransDTO.setTransNo(Sequence.getTransNo());
        pointsTransDTO.setOperator("first_operator");
        pointsTransDTO.setInstitutionId(institutionDTO.getInstitutionId());
        pointsTransDTO.setTransDate(LocalDate.now().with(TemporalAdjusters.firstDayOfMonth()).format(DateTimeFormatter.BASIC_ISO_DATE));
        pointsTransDTO.setTransTimestamp(LocalDateTime.now().with(TemporalAdjusters.firstDayOfMonth()).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
        pointsTransDTO.setDebitOrCredit(DebitOrCredit.CREDIT.getCode());

        when(institutionRepository.queryTopInstitution(eq(input.getInstitutionNo()))).thenReturn(institutionDTO);
        when(pointsTransRepository.queryPointsBySysTransNoAndStatus(eq(input.getReverseSysTransNo()), eq(TransStatus.SUCCESS.getCode()))).thenReturn(new ArrayList<>(){{
            add(pointsTransDTO);
        }});
        when(pointsTransRepository.existsAccountingTrans(eq(pointsTransDTO.getCustomerId()), eq(pointsTransDTO.getPointsTypeNo()), eq(pointsTransDTO.getTransNo()), anyBoolean())).thenReturn(false);
        when(pointsTransRepository.queryPointsTransDetailsByTransNo(eq(pointsTransDTO.getTransNo()))).thenReturn(new ArrayList<>());
        when(pointsTransRepository.queryPointsTransDetailsBySourceTransNo(eq(pointsTransDTO.getTransNo()))).thenReturn(new ArrayList<>(){{
            add(new PointsTransDetailsDTO());
        }});
        when(cacheRepository.lock(any(String.class), any(Long.class), any(TimeUnit.class))).thenReturn(true);

        ArgumentCaptor<PointsTransDTO> argumentCaptor = ArgumentCaptor.forClass(PointsTransDTO.class);
        doNothing().when(pointsTransRepository).processTrans(argumentCaptor.capture(), any());

        PointsException exception = assertThrows(PointsException.class, () -> transactionService.reversePoints(input));
        assertEquals(PointsCode.TRANS_2015.getCode(), exception.getPointsCode().getCode());

        PointsTransDTO pointsTrans = argumentCaptor.getValue();
        assertNotNull(pointsTrans);

        assertAll(
                () -> assertEquals(TransStatus.FAILED.getCode(), pointsTrans.getTransStatus()),
                () -> assertNotEquals(pointsTransDTO.getTransNo(),pointsTrans.getTransNo()),
                () -> assertNotNull(pointsTrans.getTransNo()),
                () -> assertEquals(ReversedFlag.REVERSED.getCode(), pointsTrans.getReversedFlag()),
                () -> assertEquals(input.getSysTransNo(), pointsTrans.getSysTransNo()),
                () -> assertEquals(input.getOperator(), pointsTrans.getOperator()),
                () -> assertNotNull(pointsTrans.getDescription())
        );
    }

    @Test
    void reversePoints_sys_trans_no_when_same_day_then_success() {
        PointsReverseInput input = new PointsReverseInput();
        input.setSysTransNo("sys_trans_no");
        input.setReverseSysTransNo("reverse_sys_trans_no");
        input.setInstitutionNo("inst_no");
        input.setOperator("input_operator");
        InstitutionDTO institutionDTO = getInstitutionDTO(input);

        PointsTransDTO pointsTransDTO = new PointsTransDTO();
        pointsTransDTO.setId(1L);
        pointsTransDTO.setReversedFlag(ReversedFlag.NOT_REVERSED.getCode());
        pointsTransDTO.setSysTransNo(input.getSysTransNo());
        pointsTransDTO.setTransStatus(TransStatus.SUCCESS.getCode());
        pointsTransDTO.setTransNo(Sequence.getTransNo());
        pointsTransDTO.setOperator("first_operator");
        pointsTransDTO.setInstitutionId(institutionDTO.getInstitutionId());
        pointsTransDTO.setTransDate(LocalDate.now().format(DateTimeFormatter.BASIC_ISO_DATE));
        pointsTransDTO.setTransTimestamp(LocalDateTime.now().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
        pointsTransDTO.setDebitOrCredit(DebitOrCredit.CREDIT.getCode());

        when(institutionRepository.queryTopInstitution(eq(input.getInstitutionNo()))).thenReturn(institutionDTO);
        when(pointsTransRepository.queryPointsBySysTransNoAndStatus(eq(input.getReverseSysTransNo()), eq(TransStatus.SUCCESS.getCode()))).thenReturn(new ArrayList<>(){{
            add(pointsTransDTO);
        }});
        when(pointsTransRepository.existsAccountingTrans(eq(pointsTransDTO.getCustomerId()), eq(pointsTransDTO.getPointsTypeNo()), eq(pointsTransDTO.getTransNo()), anyBoolean())).thenReturn(false);
        when(cacheRepository.lock(any(String.class), any(Long.class), any(TimeUnit.class))).thenReturn(true);

        ArgumentCaptor<PointsTransDTO> pointsTransDTOArgumentCaptor = ArgumentCaptor.forClass(PointsTransDTO.class);
        ArgumentCaptor<PointsTransDTO> expirePointsTransArgumentCaptor = ArgumentCaptor.forClass(PointsTransDTO.class);
        doNothing().when(pointsTransRepository).processTrans(pointsTransDTOArgumentCaptor.capture(), expirePointsTransArgumentCaptor.capture());

        PointsReverseOutput pointsReverseOutput = transactionService.reversePoints(input);
        assertNotNull(pointsReverseOutput);
        assertNotNull(pointsReverseOutput.getTransDate());

        PointsTransDTO pointsTrans = pointsTransDTOArgumentCaptor.getValue();
        assertNotNull(pointsTrans);

        assertAll(
                () -> assertEquals(TransStatus.SUCCESS.getCode(), pointsTrans.getTransStatus()),
                () -> assertNotEquals(pointsTransDTO.getTransNo(),pointsTrans.getTransNo()),
                () -> assertNotNull(pointsTrans.getTransNo()),
                () -> assertEquals(ReversedFlag.REVERSED.getCode(), pointsTrans.getReversedFlag()),
                () -> assertEquals(input.getSysTransNo(), pointsTrans.getSysTransNo()),
                () -> assertEquals(input.getOperator(), pointsTrans.getOperator()),
                () -> assertNotNull(pointsTrans.getDescription())
        );

        assertNull(expirePointsTransArgumentCaptor.getValue());
    }

    @Test
    void reversePoints_sys_trans_no_when_not_same_day_then_success() {
        PointsReverseInput input = new PointsReverseInput();
        input.setSysTransNo("sys_trans_no");
        input.setReverseSysTransNo("reverse_sys_trans_no");
        input.setInstitutionNo("inst_no");
        input.setOperator("input_operator");
        InstitutionDTO institutionDTO = getInstitutionDTO(input);

        PointsTransDTO pointsTransDTO = new PointsTransDTO();
        pointsTransDTO.setId(1L);
        pointsTransDTO.setReversedFlag(ReversedFlag.NOT_REVERSED.getCode());
        pointsTransDTO.setSysTransNo(input.getSysTransNo());
        pointsTransDTO.setTransStatus(TransStatus.SUCCESS.getCode());
        pointsTransDTO.setTransNo(Sequence.getTransNo());
        pointsTransDTO.setOperator("first_operator");
        pointsTransDTO.setInstitutionId(institutionDTO.getInstitutionId());
        pointsTransDTO.setTransDate(LocalDate.now().with(TemporalAdjusters.firstDayOfMonth()).format(DateTimeFormatter.BASIC_ISO_DATE));
        pointsTransDTO.setTransTimestamp(LocalDateTime.now().with(TemporalAdjusters.firstDayOfMonth()).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
        pointsTransDTO.setDebitOrCredit(DebitOrCredit.CREDIT.getCode());

        when(institutionRepository.queryTopInstitution(eq(input.getInstitutionNo()))).thenReturn(institutionDTO);
        when(pointsTransRepository.queryPointsBySysTransNoAndStatus(eq(input.getReverseSysTransNo()), eq(TransStatus.SUCCESS.getCode()))).thenReturn(new ArrayList<>(){{
            add(pointsTransDTO);
        }});
        when(pointsTransRepository.existsAccountingTrans(eq(pointsTransDTO.getCustomerId()), eq(pointsTransDTO.getPointsTypeNo()), eq(pointsTransDTO.getTransNo()), anyBoolean())).thenReturn(false);
        when(pointsTransRepository.queryPointsTransDetailsByTransNo(eq(pointsTransDTO.getTransNo()))).thenReturn(new ArrayList<>());
        when(pointsTransRepository.queryPointsTransDetailsBySourceTransNo(eq(pointsTransDTO.getTransNo()))).thenReturn(new ArrayList<>());
        when(cacheRepository.lock(any(String.class), any(Long.class), any(TimeUnit.class))).thenReturn(true);

        ArgumentCaptor<PointsTransDTO> pointsTransDTOArgumentCaptor = ArgumentCaptor.forClass(PointsTransDTO.class);
        ArgumentCaptor<PointsTransDTO> expirePointsTransArgumentCaptor = ArgumentCaptor.forClass(PointsTransDTO.class);
        doNothing().when(pointsTransRepository).processTrans(pointsTransDTOArgumentCaptor.capture(), expirePointsTransArgumentCaptor.capture());

        PointsReverseOutput pointsReverseOutput = transactionService.reversePoints(input);
        assertNotNull(pointsReverseOutput);
        assertNotNull(pointsReverseOutput.getTransDate());

        PointsTransDTO pointsTrans = pointsTransDTOArgumentCaptor.getValue();
        assertNotNull(pointsTrans);

        assertAll(
                () -> assertEquals(TransStatus.SUCCESS.getCode(), pointsTrans.getTransStatus()),
                () -> assertNotEquals(pointsTransDTO.getTransNo(),pointsTrans.getTransNo()),
                () -> assertNotNull(pointsTrans.getTransNo()),
                () -> assertEquals(ReversedFlag.REVERSED.getCode(), pointsTrans.getReversedFlag()),
                () -> assertEquals(input.getSysTransNo(), pointsTrans.getSysTransNo()),
                () -> assertEquals(input.getOperator(), pointsTrans.getOperator()),
                () -> assertNotNull(pointsTrans.getDescription())
        );

        assertNull(expirePointsTransArgumentCaptor.getValue());
    }

    @Test
    void reversePoints_sys_trans_no_when_not_same_day_and_debit_has_expired_then_success() {
        PointsReverseInput input = new PointsReverseInput();
        input.setSysTransNo("sys_trans_no");
        input.setReverseSysTransNo("reverse_sys_trans_no");
        input.setInstitutionNo("inst_no");
        input.setOperator("input_operator");
        InstitutionDTO institutionDTO = getInstitutionDTO(input);

        PointsTransDTO pointsTransDTO = new PointsTransDTO();
        pointsTransDTO.setId(1L);
        pointsTransDTO.setReversedFlag(ReversedFlag.NOT_REVERSED.getCode());
        pointsTransDTO.setSysTransNo(input.getSysTransNo());
        pointsTransDTO.setTransStatus(TransStatus.SUCCESS.getCode());
        pointsTransDTO.setTransNo(Sequence.getTransNo());
        pointsTransDTO.setOperator("first_operator");
        pointsTransDTO.setInstitutionId(institutionDTO.getInstitutionId());
        pointsTransDTO.setTransDate(LocalDate.now().with(TemporalAdjusters.firstDayOfMonth()).format(DateTimeFormatter.BASIC_ISO_DATE));
        pointsTransDTO.setTransTimestamp(LocalDateTime.now().with(TemporalAdjusters.firstDayOfMonth()).atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());
        pointsTransDTO.setDebitOrCredit(DebitOrCredit.DEBIT.getCode());
        pointsTransDTO.setPointsAmount(BigDecimal.TEN);
        pointsTransDTO.setClearingAmt(BigDecimal.ONE);

        PointsTransDetailsDTO pointsTransDetailsDTO = new PointsTransDetailsDTO();
        pointsTransDetailsDTO.setEndDate(Date.from(LocalDateTime.now().minusDays(1).atZone(ZoneId.systemDefault()).toInstant()));
        pointsTransDetailsDTO.setPointsAmount(BigDecimal.TEN);

        when(institutionRepository.queryTopInstitution(eq(input.getInstitutionNo()))).thenReturn(institutionDTO);
        when(pointsTransRepository.queryPointsBySysTransNoAndStatus(eq(input.getReverseSysTransNo()), eq(TransStatus.SUCCESS.getCode()))).thenReturn(new ArrayList<>(){{
            add(pointsTransDTO);
        }});
        when(pointsTransRepository.existsAccountingTrans(eq(pointsTransDTO.getCustomerId()), eq(pointsTransDTO.getPointsTypeNo()), eq(pointsTransDTO.getTransNo()), anyBoolean())).thenReturn(false);
        when(pointsTransRepository.queryPointsTransDetailsByTransNo(eq(pointsTransDTO.getTransNo()))).thenReturn(new ArrayList<>(){{
            add(pointsTransDetailsDTO);
        }});

        when(cacheRepository.lock(any(String.class), any(Long.class), any(TimeUnit.class))).thenReturn(true);

        ArgumentCaptor<PointsTransDTO> pointsTransDTOArgumentCaptor = ArgumentCaptor.forClass(PointsTransDTO.class);
        ArgumentCaptor<PointsTransDTO> expirePointsTransArgumentCaptor = ArgumentCaptor.forClass(PointsTransDTO.class);
        doNothing().when(pointsTransRepository).processTrans(pointsTransDTOArgumentCaptor.capture(), expirePointsTransArgumentCaptor.capture());

        PointsReverseOutput pointsReverseOutput = transactionService.reversePoints(input);
        assertNotNull(pointsReverseOutput);
        assertNotNull(pointsReverseOutput.getTransDate());

        PointsTransDTO pointsTrans = pointsTransDTOArgumentCaptor.getValue();
        assertNotNull(pointsTrans);

        assertAll(
                () -> assertEquals(TransStatus.SUCCESS.getCode(), pointsTrans.getTransStatus()),
                () -> assertNotEquals(pointsTransDTO.getTransNo(),pointsTrans.getTransNo()),
                () -> assertNotNull(pointsTrans.getTransNo()),
                () -> assertEquals(ReversedFlag.REVERSED.getCode(), pointsTrans.getReversedFlag()),
                () -> assertEquals(input.getSysTransNo(), pointsTrans.getSysTransNo()),
                () -> assertEquals(input.getOperator(), pointsTrans.getOperator()),
                () -> assertEquals(pointsTransDTO.getPointsAmount(), pointsTrans.getPointsAmount()),
                () -> assertNotNull(pointsTrans.getDescription())
        );

        PointsTransDTO expirePointsTrans = expirePointsTransArgumentCaptor.getValue();
        assertNotNull(expirePointsTransArgumentCaptor.getValue());
        assertAll(
                () -> assertEquals(TransStatus.SUCCESS.getCode(), expirePointsTrans.getTransStatus()),
                () -> assertNotEquals(pointsTransDTO.getTransNo(),expirePointsTrans.getTransNo()),
                () -> assertNotEquals(pointsTrans.getTransNo(),expirePointsTrans.getTransNo()),
                () -> assertNotNull(expirePointsTrans.getTransNo()),
                () -> assertEquals(ReversedFlag.NOT_REVERSED.getCode(), expirePointsTrans.getReversedFlag()),
                () -> assertEquals(TransType.DUE_POINT.getCode(), expirePointsTrans.getTransTypeNo()),
                () -> assertEquals(input.getSysTransNo(), expirePointsTrans.getSysTransNo()),
                () -> assertEquals(input.getOperator(), expirePointsTrans.getOperator()),
                () -> assertEquals(pointsTransDetailsDTO.getPointsAmount(), expirePointsTrans.getPointsAmount().negate()),
                () -> assertNotNull(expirePointsTrans.getDescription())
        );
    }

    private InstitutionDTO getInstitutionDTO(PointsReverseInput input) {
        InstitutionDTO institutionDTO = new InstitutionDTO();
        institutionDTO.setTopInstitutionId("0");
        institutionDTO.setParentInstitutionId("0");
        institutionDTO.setInstitutionId("1234");
        institutionDTO.setInstitutionNo(input.getInstitutionNo());
        institutionDTO.setInstitutionName("instName");
        institutionDTO.setDescription("instDesc");
        return institutionDTO;
    }
}