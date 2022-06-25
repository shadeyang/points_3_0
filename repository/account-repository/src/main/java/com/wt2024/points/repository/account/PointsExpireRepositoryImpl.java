package com.wt2024.points.repository.account;

import com.wt2024.points.common.Constants;
import com.wt2024.points.repository.account.converter.ConverterConstants;
import com.wt2024.points.repository.account.entity.PointsTransExpire;
import com.wt2024.points.repository.account.mapper.PointsTransExpireMapper;
import com.wt2024.points.repository.api.account.domain.PointsExpireDTO;
import com.wt2024.points.repository.api.account.repository.PointsExpireRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.util.CollectionUtils;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * @author Shade.Yang [shade.yang@aliyun.com]
 * @date 2022/2/16 12:47
 * @project points3.0:com.wt2024.points.repository.account
 */
@Slf4j
@Repository
public class PointsExpireRepositoryImpl implements PointsExpireRepository {

    @Autowired
    PointsTransExpireMapper pointsTransExpireMapper;

    @Override
    public List<PointsExpireDTO> queryCustomerByExpire(long fromId, int index, int pageSize) {
        index = index <= 0 ? Constants.PAGE.DEFAULT_INDEX : index;
        pageSize = pageSize == 0 ? Constants.PAGE.DEFAULT_PAGE_SIZE : pageSize;
        List<PointsTransExpire> pointsTransExpireList = pointsTransExpireMapper.selectPointsTransExpireByPage(fromId, (index - 1) * pageSize, pageSize);
        Map<String, PointsExpireDTO> tempMap = new LinkedHashMap<>();
        pointsTransExpireList.stream().forEach(pointsExpire -> {
            String mapKey = pointsExpire.getCustomerId() + "_" + pointsExpire.getPointsTypeNo();
            PointsExpireDTO expire = tempMap.get(mapKey);
            if (Objects.isNull(expire) || expire.getId() < pointsExpire.getId()) {
                tempMap.put(mapKey, ConverterConstants.POINTS_TRANS_EXPIRE_CONVERTER.toPointsExpireDTO(pointsExpire));
            }
        });
        return tempMap.values().stream().collect(Collectors.toList());
    }

    @Override
    public List<String> queryPointsExpireByCustomerIdAndPointsTypeNo(String customerId, String pointsTypeNo) {
        return pointsTransExpireMapper.selectPointsExpireByCustomerIdAndPointsTypeNo(customerId, pointsTypeNo);
    }

    @Override
    public int deletePointsExpireWhenBalanceZero(String customerId, String pointsTypeNo, List<String> transNoList) {
        if (CollectionUtils.isEmpty(transNoList)) {
            return 0;
        }
        return pointsTransExpireMapper.deleteByTransNo(customerId, pointsTypeNo, transNoList);
    }

}
