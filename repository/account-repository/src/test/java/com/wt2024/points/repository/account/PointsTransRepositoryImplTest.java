package com.wt2024.points.repository.account;

import com.wt2024.points.common.enums.TransStatus;
import com.wt2024.points.repository.account.mapper.PointsTransMapper;
import com.wt2024.points.repository.api.account.domain.PointsAccountDetailsDTO;
import com.wt2024.points.repository.api.account.domain.PointsTransDTO;
import org.h2.tools.RunScript;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.util.CollectionUtils;

import javax.sql.DataSource;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * @author Shade.Yang [shade.yang@aliyun.com]
 * @date 2022/1/25 17:32
 * @project points3.0:com.wt2024.points.repository.account
 */
@SpringBootTest
class PointsTransRepositoryImplTest {

    @Autowired
    private PointsTransRepositoryImpl pointsTransRepository;
    @Autowired
    private PointsTransMapper pointsTransMapper;
    @Autowired
    private DataSource dataSource;

    @BeforeEach
    void setUp() throws SQLException, FileNotFoundException {
        RunScript.execute(dataSource.getConnection(), new FileReader("src/test/resources/db_script/points_trans.sql"));
        RunScript.execute(dataSource.getConnection(), new FileReader("src/test/resources/db_script/points_trans_temp.sql"));
        RunScript.execute(dataSource.getConnection(), new FileReader("src/test/resources/db_script/points_trans_details.sql"));
        RunScript.execute(dataSource.getConnection(), new FileReader("src/test/resources/db_script/points_type.sql"));
        RunScript.execute(dataSource.getConnection(), new FileReader("src/test/resources/db_script/points_account_info.sql"));
    }

    @Test
    void queryAccountingTrans() {
        String customerId = "844703788571820032";
        String pointsTypeNo = "844700918006939648";
        List<PointsTransDTO> pointsTransDTOList = pointsTransRepository.queryAccountingTrans(customerId, pointsTypeNo);
        assertNotNull(pointsTransDTOList);
    }

    @Test
    void queryPointsAccountDetails() {
        String customerId = "844703788571820032";
        String pointsTypeNo = "844700918006939648";
        List<PointsAccountDetailsDTO> pointsAccountDetailsDTOList = pointsTransRepository.queryPointsAccountDetails(customerId, pointsTypeNo, new ArrayList<>());
        assertNotNull(pointsAccountDetailsDTOList);
    }

    @Test
    void queryPointsTransByPage() {
        String customerId = "844703788571820032";
        String institutionId = "844669109470756864";
        String pointsTypeNo = "844700918006939648";
        long fromId = 0;
        List<PointsTransDTO> pointsTransDTOList = pointsTransRepository.queryPointsTransByPage(customerId, institutionId, pointsTypeNo, fromId, null, null, 0, 0);
        assertNotNull(pointsTransDTOList);
    }

    @Test
    void queryPointsTransByPage_pointsType_invalid() {
        String customerId = "844703788571820032";
        String institutionId = "844669109470756864";
        String pointsTypeNo = "not_exists_points_type";
        long fromId = 0;
        List<PointsTransDTO> pointsTransDTOList = pointsTransRepository.queryPointsTransByPage(customerId, institutionId, pointsTypeNo, fromId, null, null, 0, 0);
        assertTrue(CollectionUtils.isEmpty(pointsTransDTOList));
    }

    @Test
    void existsPointsBySysTransNo() {
        String sysTransNo = "880527837759537152";
        PointsTransDTO pointsTransDTO = pointsTransRepository.existsPointsBySysTransNo(sysTransNo);
        assertNotNull(pointsTransDTO);
    }

    @Test
    void queryPointsBySysTransNoAndStatus() {
        String sysTransNo = "880527837759537152";
        String status = TransStatus.SUCCESS.getCode();
        List<PointsTransDTO> pointsTransDTOList = pointsTransRepository.queryPointsBySysTransNoAndStatus(sysTransNo, status);
        assertFalse(CollectionUtils.isEmpty(pointsTransDTOList));
    }

    @Test
    void existsAccountingTrans_include_true() {
        String customerId = "844703788571820032";
        String pointsTypeNo = "844700918006939648";
        String transNo = "849779142810402818";
        boolean include = true;

        assertTrue(pointsTransRepository.existsAccountingTrans(customerId, pointsTypeNo, transNo, include));
    }

    @Test
    void existsAccountingTrans_include_false() {
        String customerId = "844703788571820032";
        String pointsTypeNo = "844700918006939648";
        String transNo = "849779142810402818";
        boolean include = false;

        assertFalse(pointsTransRepository.existsAccountingTrans(customerId, pointsTypeNo, transNo, include));
    }

    @Test
    void queryPointsTransDetailsByTransNo() {
        String transNo = "849779142793625600";

        assertFalse(CollectionUtils.isEmpty(pointsTransRepository.queryPointsTransDetailsByTransNo(transNo)));
    }

    @Test
    void queryPointsTransDetailsBySourceTransNo() {
        String transNo = "849779142810402816";

        assertTrue(CollectionUtils.isEmpty(pointsTransRepository.queryPointsTransDetailsBySourceTransNo(transNo)));
    }

    @Test
    void queryAllWantToAsync() {
        String customerId = "CUST942719723039117312";
        String pointsTypeNo = "844700918006939648";
        Long startIndex = 1L;
        Integer pageSize = 10;
        assertTrue(CollectionUtils.isEmpty(pointsTransRepository.queryAllWantToAsync(customerId, pointsTypeNo, startIndex, pageSize)));
    }

    @Test
    void queryPointsByOldTransNoAndStatus(){
        String transNo = "849779142810402818";
        assertTrue(CollectionUtils.isEmpty(pointsTransRepository.queryPointsByOldTransNoAndStatus(transNo,TransStatus.SUCCESS.getCode())));
    }

}