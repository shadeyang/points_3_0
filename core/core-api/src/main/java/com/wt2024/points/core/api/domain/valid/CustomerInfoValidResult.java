package com.wt2024.points.core.api.domain.valid;

import lombok.*;

import java.io.Serializable;

/**
 * @Author Shade.Yang [shade.yang@aliyun.com]
 * @Date 2021/5/26 15:37
 * @Project points2.0:com.wt2024.points.service.domain.as
 */
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CustomerInfoValidResult implements Serializable {

    private Institution institution;

    private Customer customer;

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Institution {
        private String institutionId;

        private String institutionNo;

        private String institutionName;

        private String parentInstitutionId;

        private String topInstitutionId;

        private String description;
    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Customer {
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

    public String getInstitutionId() {
        return this.institution == null ? null : this.institution.getInstitutionId();
    }

    public String getCustomerId() {
        return this.customer == null ? null : this.customer.getCustomerId();
    }
}
