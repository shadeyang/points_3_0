package com.wt2024.points.repository.api.system.repository;

import com.wt2024.points.repository.api.system.domain.InstitutionDTO;

import java.util.List;

/**
 * @ClassName InstitutionRepository
 * @Description: TODO
 * @Author shade.yang
 * @Date 2022/1/1
 * @Version V1.0
 **/
public interface InstitutionRepository {

    InstitutionDTO queryTopInstitution(String institutionNo);

    List<InstitutionDTO> queryInstitutionByTop(String topInstitutionId);

    Integer createInstitution(InstitutionDTO institutionDTO);

    Integer updateInstitution(InstitutionDTO institutionDTO);

    Integer deleteInstitutionId(String institutionId);

    List<InstitutionDTO> selectByParentInstitutionId(String parentInstitutionId);
}
