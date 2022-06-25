package com.wt2024.points.repository.api.customer.domain;

import lombok.Getter;
import lombok.Setter;

/**
 * @ClassName CustomerDTO
 * @Description: TODO
 * @Author shade.yang
 * @Date 2022/1/1
 * @Version V1.0
 **/
@Getter
@Setter
public class CustomerDTO {

    private String customerId;

    private String customerName;

    private String gender;

    private String phoneNumber;

    private String email;

    private String address;

    private String birthdate;

    private String institutionId;

    private String operator;

    private String customerLvl;

}
