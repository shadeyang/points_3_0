package com.wt2024.points.core.converter;

import com.wt2024.points.core.api.domain.valid.CustomerInfoValidResult;
import com.wt2024.points.repository.api.system.domain.InstitutionDTO;
import org.mapstruct.Mapper;

/**
 * @ClassName InstitutionConverter
 * @Description: TODO
 * @Author shade.yang
 * @Date 2022/1/1
 * @Version V1.0
 **/
@Mapper
public interface InstitutionConverter {

    CustomerInfoValidResult.Institution toInstitution(InstitutionDTO institutionDTO);

}
