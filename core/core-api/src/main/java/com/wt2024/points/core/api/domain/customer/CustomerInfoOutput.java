package com.wt2024.points.core.api.domain.customer;

import com.wt2024.points.common.enums.Gender;
import com.wt2024.points.core.api.domain.OutputBase;
import com.wt2024.points.core.api.domain.account.PointsAccountInfo;
import lombok.*;

import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CustomerInfoOutput extends OutputBase {

    private String customerId;

    private String customerName;

    private Gender gender;

    private String phoneNumber;

    private String email;

    private String address;

    private String birthdate;

    private List<PointsAccountInfo> pointsAccountInfoList;

}
