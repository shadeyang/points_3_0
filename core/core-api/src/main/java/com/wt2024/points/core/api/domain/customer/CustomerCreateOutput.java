package com.wt2024.points.core.api.domain.customer;

import com.wt2024.points.core.api.domain.OutputBase;
import lombok.*;

/**
 * @ClassName CustomerCreateOutputDomain
 * @Description: TODO
 * @Author shade.yang
 * @Date 2021/5/8
 * @Version V1.0
 **/
@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CustomerCreateOutput extends OutputBase {

    private String customerId;

}
