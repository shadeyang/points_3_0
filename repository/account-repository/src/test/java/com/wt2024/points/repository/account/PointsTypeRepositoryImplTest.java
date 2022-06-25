package com.wt2024.points.repository.account;

import com.wt2024.points.repository.account.mapper.PointsTypeMapper;
import com.wt2024.points.repository.api.account.domain.PointsTypeDTO;
import org.h2.tools.RunScript;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.sql.DataSource;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
class PointsTypeRepositoryImplTest {

    @Autowired
    private PointsTypeRepositoryImpl pointsTypeRepository;
    @Autowired
    private PointsTypeMapper pointsTypeMapper;
    @Autowired
    private DataSource dataSource;

    @BeforeEach
    void setUp() throws SQLException, FileNotFoundException {
        RunScript.execute(dataSource.getConnection(), new FileReader("src/test/resources/db_script/points_type.sql"));
    }

    @Test
    void queryPointsTypeByInst() {
        String pointsTypeNo = "844703788584402944";
        String institutionId = "844669109470756864";
        PointsTypeDTO pointsTypeDTO = pointsTypeRepository.queryPointsTypeByInst(pointsTypeNo,institutionId);
        assertNotNull(pointsTypeDTO);
    }
}