package com.wt2024.points.repository.account;

import com.wt2024.points.common.sequence.Sequence;
import com.wt2024.points.repository.account.converter.ConverterConstants;
import com.wt2024.points.repository.account.entity.PointsType;
import com.wt2024.points.repository.account.mapper.PointsTypeMapper;
import com.wt2024.points.repository.api.account.domain.PointsTypeDTO;
import com.wt2024.points.repository.api.account.repository.PointsTypeRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @ClassName PointsTypeRepositoryImpl
 * @Description: TODO
 * @Author shade.yang
 * @Date 2022/1/28
 * @Version V1.0
 **/
@Slf4j
@Repository
public class PointsTypeRepositoryImpl implements PointsTypeRepository {
    @Autowired
    private PointsTypeMapper pointsTypeMapper;

    @Override
    public PointsTypeDTO queryPointsTypeByInst(String pointsTypeNo, String institutionId) {
        PointsType pointsType = pointsTypeMapper.selectByInstitutionAndPointsType(institutionId, pointsTypeNo);
        return ConverterConstants.POINTS_TYPE_CONVERTER.toPointsTypeDTO(pointsType);
    }

    @Override
    public List<PointsTypeDTO> queryPointsTypeListByInst(String institutionId) {
        return pointsTypeMapper.selectByInstitutionId(institutionId).stream().map(ConverterConstants.POINTS_TYPE_CONVERTER::toPointsTypeDTO).collect(Collectors.toList());
    }

    @Override
    public Integer createPointsType(PointsTypeDTO pointsTypeDTO) {
        PointsType pointsType = ConverterConstants.POINTS_TYPE_CONVERTER.toPointsType(pointsTypeDTO);
        pointsType.setPointsTypeNo(String.valueOf(Sequence.getId()));
        return pointsTypeMapper.insert(pointsType);
    }

    @Override
    public Integer updatePointsType(PointsTypeDTO pointsTypeDTO) {
        PointsType pointsType = ConverterConstants.POINTS_TYPE_CONVERTER.toPointsType(pointsTypeDTO);
        return pointsTypeMapper.updateByPointsTypeNo(pointsType);
    }

    @Override
    public Integer deletePointsType(PointsTypeDTO pointsTypeDTO) {
        return pointsTypeMapper.deleteByPointsTypeNo(pointsTypeDTO.getPointsTypeNo(),pointsTypeDTO.getInstitutionId());
    }
}
