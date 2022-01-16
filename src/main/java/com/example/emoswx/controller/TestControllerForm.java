package com.example.emoswx.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

/**
 * @author ZZy
 * @date 2022/1/8 14:20
 * @description
 */
@Data
@ApiModel
public class TestControllerForm {

//    @Pattern(regexp="[\\u4e00-\\u9fa5]{2,15}")
    @NotBlank
    @ApiModelProperty("姓名")
    private String name;
}
