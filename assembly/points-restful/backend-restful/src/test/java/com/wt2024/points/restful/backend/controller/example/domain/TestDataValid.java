package com.wt2024.points.restful.backend.controller.example.domain;

import com.wt2024.points.restful.backend.domain.Base;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotEmpty;

/**
 * @ClassName TestDataDomain
 * @Description: TODO
 * @Author shade.yang
 * @Date 2021/5/8
 * @Version V1.0
 **/
@Getter
@Setter
public class TestDataValid extends Base {

    @NotEmpty
    private String valid;
}
