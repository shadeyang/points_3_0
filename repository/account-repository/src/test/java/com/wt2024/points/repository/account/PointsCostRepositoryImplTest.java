package com.wt2024.points.repository.account;

import com.wt2024.points.repository.api.account.repository.PointsCostRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * @author Shade.Yang [shade.yang@aliyun.com]
 * @date 2022/5/31 14:14
 * @project points3.0:com.wt2024.points.repository.account
 */
@SpringBootTest
class PointsCostRepositoryImplTest {

    @Autowired
    PointsCostRepository pointsCostRepository;

    @Test
    void queryPointsCostById() {
        Assertions.assertDoesNotThrow(()->pointsCostRepository.queryPointsCostById("1","institutionId"));
    }
    @Test
    void queryAllPointsCostByInstitutionId(){
        Assertions.assertDoesNotThrow(()->pointsCostRepository.queryAllPointsCostByInstitutionId("institutionId"));
    }

    @Test
    void queryAllPointsCostByInstitutionId_not_exists(){
        Assertions.assertDoesNotThrow(()->pointsCostRepository.queryAllPointsCostByInstitutionId(""));
    }
}