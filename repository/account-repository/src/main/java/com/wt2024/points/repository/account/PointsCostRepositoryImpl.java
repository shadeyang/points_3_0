package com.wt2024.points.repository.account;

import com.wt2024.points.common.sequence.Sequence;
import com.wt2024.points.repository.account.converter.ConverterConstants;
import com.wt2024.points.repository.account.entity.PointsCost;
import com.wt2024.points.repository.account.mapper.PointsCostMapper;
import com.wt2024.points.repository.api.account.domain.PointsCostDTO;
import com.wt2024.points.repository.api.account.repository.PointsCostRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @author Shade.Yang [shade.yang@aliyun.com]
 * @date 2022/5/31 09:57
 * @project points3.0:com.wt2024.points.repository.account
 */
@Slf4j
@Repository
public class PointsCostRepositoryImpl implements PointsCostRepository {

    @Autowired
    PointsCostMapper pointsCostMapper;

    @Override
    public PointsCostDTO queryPointsCostById(String costLine, String institutionId) {
        return ConverterConstants.POINTS_COST_CONVERTER.toPointsCostDTO(pointsCostMapper.selectByCostLine(costLine,institutionId));
    }

    @Override
    public List<PointsCostDTO> queryAllPointsCostByInstitutionId(String institutionId) {
        return pointsCostMapper.selectByInstitutionId(institutionId).stream().map(ConverterConstants.POINTS_COST_CONVERTER::toPointsCostDTO).collect(Collectors.toList());
    }

    @Override
    public Integer createPointsCost(PointsCostDTO pointsCostDTO) {
        PointsCost pointsCost = ConverterConstants.POINTS_COST_CONVERTER.toPointsCost(pointsCostDTO);
        pointsCost.setCostLine(String.valueOf(Sequence.getId()));
        return pointsCostMapper.insert(pointsCost);
    }

    @Override
    public Integer updatePointsCost(PointsCostDTO pointsCostDTO) {
        PointsCost pointsCost = ConverterConstants.POINTS_COST_CONVERTER.toPointsCost(pointsCostDTO);
        return pointsCostMapper.updateByCostLine(pointsCost);
    }

    @Override
    public Integer deletePointsCost(PointsCostDTO pointsCostDTO) {
        return pointsCostMapper.deleteByCostLine(pointsCostDTO.getCostLine(),pointsCostDTO.getInstitutionId());
    }
}
