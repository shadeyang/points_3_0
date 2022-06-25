package com.wt2024.points.repository.api.system.domain;

import lombok.Getter;
import lombok.Setter;

/**
 * @ClassName InstitutionDTO
 * @Description: TODO
 * @Author shade.yang
 * @Date 2022/1/1
 * @Version V1.0
 **/
@Getter
@Setter
public class InstitutionDTO {

    private String institutionId;

    private String institutionNo;

    private String institutionName;

    private String parentInstitutionId;

    private String topInstitutionId;

    private String description;

}
