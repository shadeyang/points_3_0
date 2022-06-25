package com.wt2024.points.repository.system;

import com.wt2024.points.repository.api.system.domain.InstitutionDTO;
import com.wt2024.points.repository.api.system.repository.InstitutionRepository;
import com.wt2024.points.repository.system.entity.Institution;
import com.wt2024.points.repository.system.mapper.InstitutionMapper;
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
class InstitutionRepositoryImplTest {

    @Autowired
    InstitutionRepository institutionRepository;
    @Autowired
    InstitutionMapper institutionMapper;

    @Autowired
    private DataSource dataSource;

    @BeforeEach
    void setUp() throws SQLException, FileNotFoundException {
        RunScript.execute(dataSource.getConnection(), new FileReader("src/test/resources/db_script/institution.sql"));
    }

    @Test
    void queryTopInstitution_institution_not_exists() {
        InstitutionDTO institutionDTO = institutionRepository.queryTopInstitution("not_exists");
        assertNull(institutionDTO);
    }

    @Test
    void queryTopInstitution_institution_exists() {
        String institutionNo = "100000";
        InstitutionDTO institutionDTO = institutionRepository.queryTopInstitution(institutionNo);
        assertNotNull(institutionDTO);
        Institution institution = new Institution();
        institution.setInstitutionNo(institutionNo);
        institution.setTopInstitutionId("0");
        institution.setParentInstitutionId("0");
        Institution institutionResult = institutionMapper.selectFirstOne(institution);
        assertNotNull(institutionDTO);
        assertAll(
                ()->assertEquals(institutionResult.getInstitutionId(),institutionDTO.getInstitutionId()),
                ()->assertEquals(institutionResult.getInstitutionNo(),institutionDTO.getInstitutionNo()),
                ()->assertEquals(institutionResult.getInstitutionName(),institutionDTO.getInstitutionName()),
                ()->assertEquals(institutionResult.getParentInstitutionId(),institutionDTO.getParentInstitutionId()),
                ()->assertEquals(institutionResult.getTopInstitutionId(),institutionDTO.getTopInstitutionId()),
                ()->assertEquals(institutionResult.getDescription(),institutionDTO.getDescription())
        );
    }

    @Test
    void queryInstitution_by_topInstitutionId(){
        List<InstitutionDTO> institutionDTOList = institutionRepository.queryInstitutionByTop("0");
        assertFalse(institutionDTOList.isEmpty());
    }
}