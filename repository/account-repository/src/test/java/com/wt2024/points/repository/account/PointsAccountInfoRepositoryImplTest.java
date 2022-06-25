package com.wt2024.points.repository.account;


import com.wt2024.points.repository.account.mapper.PointsAccountInfoMapper;
import com.wt2024.points.repository.api.account.domain.PointsAccountInfoDTO;
import com.wt2024.points.repository.api.account.repository.PointsAccountInfoRepository;
import org.h2.tools.RunScript;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.sql.DataSource;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Shade.Yang [shade.yang@aliyun.com]
 * @date 2021/12/17 14:53
 * @project points3.0:com.wt2024.points.repository.account
 */
@SpringBootTest
class PointsAccountInfoRepositoryImplTest {

    @Autowired
    private PointsAccountInfoRepository pointsAccountInfoRepository;
    @Autowired
    private PointsAccountInfoMapper pointsAccountInfoMapper;
    @Autowired
    private DataSource dataSource;

    @BeforeEach
    void setUp() throws SQLException, FileNotFoundException {
        RunScript.execute(dataSource.getConnection(), new FileReader("src/test/resources/db_script/points_account_info.sql"));
        RunScript.execute(dataSource.getConnection(), new FileReader("src/test/resources/db_script/points_type.sql"));
    }

    @Test
    void queryPointsAccountInfo_with_institutionId_not_exists() {
        String customersId = "";
        String pointTypeNo = "";
        String institutionId = "not_exists";
        List<PointsAccountInfoDTO> pointsAccountInfoDTOList = pointsAccountInfoRepository.queryPointsAccountInfo(customersId, pointTypeNo, institutionId);
        Assertions.assertTrue(pointsAccountInfoDTOList.isEmpty());
    }

    @Test
    void queryPointsAccountInfo_with_pointTypeNo_is_null() {
        String customersId = "";
        String pointTypeNo = null;
        String institutionId = "844669109470756864";
        List<PointsAccountInfoDTO> pointsAccountInfoDTOList = pointsAccountInfoRepository.queryPointsAccountInfo(customersId, pointTypeNo, institutionId);
        Assertions.assertTrue(pointsAccountInfoDTOList.isEmpty());
    }

    @Test
    void queryPointsAccountInfo_with_pointTypeNo_not_exists() {
        String customersId = "";
        String pointTypeNo = "not_exists";
        String institutionId = "844669109470756864";
        List<PointsAccountInfoDTO> pointsAccountInfoDTOList = pointsAccountInfoRepository.queryPointsAccountInfo(customersId, pointTypeNo, institutionId);
        Assertions.assertTrue(pointsAccountInfoDTOList.isEmpty());
    }

    @Test
    void queryPointsAccountInfo_with_account_not_exists() {
        String customersId = "not_exists";
        String pointTypeNo = "844703788584402944";
        String institutionId = "844669109470756864";
        List<PointsAccountInfoDTO> pointsAccountInfoDTOList = pointsAccountInfoRepository.queryPointsAccountInfo(customersId, pointTypeNo, institutionId);
        Assertions.assertEquals(1, pointsAccountInfoDTOList.size());
        PointsAccountInfoDTO pointsAccountInfoDTO = pointsAccountInfoDTOList.get(0);
        Assertions.assertAll(
                () -> Assertions.assertEquals("0", pointsAccountInfoDTO.getPointsAccountStatus()),
                () -> Assertions.assertEquals(pointTypeNo, pointsAccountInfoDTO.getPointsTypeNo()),
                () -> Assertions.assertEquals(BigDecimal.ZERO, pointsAccountInfoDTO.getFreezingPoints()),
                () -> Assertions.assertEquals(BigDecimal.ZERO, pointsAccountInfoDTO.getPointsBalance()),
                () -> Assertions.assertEquals(BigDecimal.ZERO, pointsAccountInfoDTO.getInTransitPoints()),
                () -> Assertions.assertEquals(customersId, pointsAccountInfoDTO.getCustomerId())
        );
        Assertions.assertNotNull(pointsAccountInfoMapper.selectPointsAccountInfoByPointsTypeNo(customersId, new ArrayList<>() {{
            add(pointTypeNo);
        }}).stream().findFirst().get());
    }

    @Test
    void queryPointsAccountInfo_with_single_pointsType() {
        String customersId = "844703788571820032";
        String pointTypeNo = "844700918006939648";
        String institutionId = "844669109470756864";
        List<PointsAccountInfoDTO> pointsAccountInfoDTOList = pointsAccountInfoRepository.queryPointsAccountInfo(customersId, pointTypeNo, institutionId);
        Assertions.assertEquals(2, pointsAccountInfoDTOList.size());
        PointsAccountInfoDTO pointsAccountInfoDTO = pointsAccountInfoDTOList.get(0);
        Assertions.assertAll(
                () -> Assertions.assertEquals("0", pointsAccountInfoDTO.getPointsAccountStatus()),
                () -> Assertions.assertEquals(pointTypeNo, pointsAccountInfoDTO.getPointsTypeNo()),
                () -> Assertions.assertEquals(new BigDecimal("20.00"), pointsAccountInfoDTO.getFreezingPoints()),
                () -> Assertions.assertEquals(new BigDecimal("100.00"), pointsAccountInfoDTO.getPointsBalance()),
                () -> Assertions.assertEquals(new BigDecimal("30.00"), pointsAccountInfoDTO.getInTransitPoints()),
                () -> Assertions.assertEquals(customersId, pointsAccountInfoDTO.getCustomerId())
        );
    }

    @Test
    void queryPointsAccountInfo_with_pointsType_is_null() {
        String customersId = "844703788571820032";
        String pointTypeNo = null;
        String institutionId = "844669109470756864";
        List<PointsAccountInfoDTO> pointsAccountInfoDTOList = pointsAccountInfoRepository.queryPointsAccountInfo(customersId, pointTypeNo, institutionId);
        Assertions.assertTrue(pointsAccountInfoDTOList.size() > 1);
    }

    @Test
    void queryPointsAccountInfoByType() {
        String customersId = "844703788571820032";
        String pointTypeNo = "844700918006939648";
        PointsAccountInfoDTO pointsAccountInfoDTO = pointsAccountInfoRepository.queryPointsAccountInfoByType(customersId, pointTypeNo);
        Assertions.assertAll(
                () -> Assertions.assertEquals("0", pointsAccountInfoDTO.getPointsAccountStatus()),
                () -> Assertions.assertEquals(pointTypeNo, pointsAccountInfoDTO.getPointsTypeNo()),
                () -> Assertions.assertEquals(new BigDecimal("20.00"), pointsAccountInfoDTO.getFreezingPoints()),
                () -> Assertions.assertEquals(new BigDecimal("100.00"), pointsAccountInfoDTO.getPointsBalance()),
                () -> Assertions.assertEquals(new BigDecimal("30.00"), pointsAccountInfoDTO.getInTransitPoints()),
                () -> Assertions.assertEquals(customersId, pointsAccountInfoDTO.getCustomerId())
        );
    }
}