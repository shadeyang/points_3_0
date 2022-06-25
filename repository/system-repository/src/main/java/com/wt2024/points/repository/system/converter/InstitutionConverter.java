package com.wt2024.points.repository.system.converter;

import com.wt2024.points.repository.api.system.domain.InstitutionDTO;
import com.wt2024.points.repository.system.entity.Institution;
import org.mapstruct.Mapper;

/**
 * @ClassName InstitutionConverter
 * @Description: TODO
 * @Author shade.yang
 * @Date 2022/1/2
 * @Version V1.0
 **/
@Mapper
public interface InstitutionConverter {

    InstitutionDTO toInstitutionDTO(Institution institution);

    Institution toInstitution(InstitutionDTO institutionDTO);
}
