package com.example.emoswx.controller.form;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * @ClassName SearchMessageByPageForm
 * @Date 2022/2/10 17:28
 * @Author Admin
 * @Description
 */
@Data
@ApiModel
public class SearchMessageByPageForm {

    @NotNull
    @Min(1)
    private Integer page;

    @NotNull
    @Range(min = 1, max = 40)
    private Integer length;

}
