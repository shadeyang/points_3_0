package com.wt2024.points.repository.system;

import com.wt2024.points.common.exception.PointsException;
import com.wt2024.points.common.sequence.Sequence;
import com.wt2024.points.repository.api.system.domain.InstitutionDTO;
import com.wt2024.points.repository.api.system.repository.InstitutionRepository;
import com.wt2024.points.repository.system.converter.ConverterConstants;
import com.wt2024.points.repository.system.entity.Institution;
import com.wt2024.points.repository.system.mapper.InstitutionMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static com.wt2024.points.common.code.PointsCode.*;

/**
 * @ClassName InstitutionRepositoryImpl
 * @Description: TODO
 * @Author shade.yang
 * @Date 2022/1/2
 * @Version V1.0
 **/
@Repository
@Slf4j
public class InstitutionRepositoryImpl implements InstitutionRepository {

    @Autowired
    protected InstitutionMapper institutionMapper;

    private final static String DEFAULT_TOP_INSTITUTION_ID = "0";

    @Override
    public InstitutionDTO queryTopInstitution(String institutionNo) {
        Institution institution = new Institution();
        institution.setInstitutionNo(institutionNo);
        institution.setTopInstitutionId(DEFAULT_TOP_INSTITUTION_ID);
        institution.setParentInstitutionId(DEFAULT_TOP_INSTITUTION_ID);
        return ConverterConstants.INSTITUTION_CONVERTER.toInstitutionDTO(institutionMapper.selectFirstOne(institution));
    }

    @Override
    public List<InstitutionDTO> queryInstitutionByTop(String topInstitutionId) {
        return institutionMapper.selectByTopInstitutionId(topInstitutionId).stream().map(ConverterConstants.INSTITUTION_CONVERTER::toInstitutionDTO).collect(Collectors.toList());
    }

    @Override
    public Integer createInstitution(InstitutionDTO institutionDTO) {
        Institution institution = ConverterConstants.INSTITUTION_CONVERTER.toInstitution(institutionDTO);
        if(DEFAULT_TOP_INSTITUTION_ID.equals(institution.getParentInstitutionId())){
            institution.setTopInstitutionId(DEFAULT_TOP_INSTITUTION_ID);
        }else{
            Institution parentInstitution = institutionMapper.selectByInstitutionId(institution.getParentInstitutionId());
            if(DEFAULT_TOP_INSTITUTION_ID.equals(parentInstitution.getTopInstitutionId())){
                institution.setTopInstitutionId(parentInstitution.getInstitutionId());
            }else {
                institution.setTopInstitutionId(parentInstitution.getTopInstitutionId());
            }
        }
        institution.setInstitutionId(String.valueOf(Sequence.getId()));

        Institution check = findExistsInstitution(institution);
        if (Objects.nonNull(check)) {
            throw new PointsException(TRANS_1103, institution.getInstitutionNo());
        }
        
        return institutionMapper.insert(institution);
    }

    private Institution findExistsInstitution(Institution institution) {
        Institution check = new Institution();
        check.setInstitutionNo(institution.getInstitutionNo());
        check.setTopInstitutionId(institution.getTopInstitutionId());
        return institutionMapper.selectFirstOne(check);
    }

    @Override
    public Integer updateInstitution(InstitutionDTO institutionDTO) {
        Institution institution = ConverterConstants.INSTITUTION_CONVERTER.toInstitution(institutionDTO);
        Institution check = findExistsInstitution(institution);
        if (Objects.isNull(check)) {
            throw new PointsException(TRANS_1101, institution.getInstitutionNo());
        }
        if (!check.getTopInstitutionId().equals(institution.getTopInstitutionId()) || !check.getParentInstitutionId().equals(institution.getParentInstitutionId()) || !check.getInstitutionId().equals(institution.getInstitutionId())) {
            throw new PointsException(TRANS_1104);
        }
        return institutionMapper.updateInfo(institution);
    }

    @Override
    public Integer deleteInstitutionId(String institutionId) {
        return institutionMapper.deleteByInstitutionId(institutionId);
    }

    @Override
    public List<InstitutionDTO> selectByParentInstitutionId(String parentInstitutionId) {
        return institutionMapper.selectByParentInstitutionId(parentInstitutionId).stream().map(ConverterConstants.INSTITUTION_CONVERTER::toInstitutionDTO).collect(Collectors.toList());
    }
}
