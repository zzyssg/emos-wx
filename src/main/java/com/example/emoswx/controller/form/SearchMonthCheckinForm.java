package com.example.emoswx.controller.form;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotNull;

/**
 * @ClassName SearchMonthCheckinForm
 * @Date 2022/2/8 16:35
 * @Author Admin
 * @Description
 */
@Data
@ApiModel
public class SearchMonthCheckinForm {

    @NotNull
    @Range(min=2000,max=3000)
    private Integer year;

    @NotNull
    @Range(min=1,max = 12)
    private Integer month;
}
