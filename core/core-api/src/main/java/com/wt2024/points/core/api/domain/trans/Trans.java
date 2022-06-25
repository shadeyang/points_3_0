package com.wt2024.points.core.api.domain.trans;

import com.wt2024.points.common.enums.TransStatus;
import com.wt2024.points.common.enums.TransType;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * @author Shade.Yang [shade.yang@aliyun.com]
 * @date 2022/1/26 09:19
 * @project points3.0:com.wt2024.points.core.api.domain.trans
 */
@Getter
@Setter
public class Trans implements Serializable {

    private String transNo;

    private String transDate;

    private String transTime;

    private TransType transType;

    private String oldTransNo;

    private String transChannel;

    private TransStatus transStatus;

    private String sysTransNo;

    private Integer rulesId;

}
