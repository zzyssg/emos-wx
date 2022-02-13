package com.example.emoswx.controller.form;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @ClassName searchMembersForm
 * @Date 2022/2/13 16:07
 * @Author Admin
 * @Description
 */
@Data
@ApiModel
public class SearchMembersForm {

    @NotBlank
    private String members;




}
