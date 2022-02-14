package com.example.emoswx.controller.form;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * @author ZZy
 * @date 2022/2/14 17:10
 * @description
 */
@Data
@ApiModel
public class SearchMeetingByIdForm {

    @NotNull
    @Min(1)
    private Integer id;


}
