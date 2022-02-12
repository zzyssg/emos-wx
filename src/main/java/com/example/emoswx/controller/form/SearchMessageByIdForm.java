package com.example.emoswx.controller.form;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

/**
 * @ClassName SearchMessageById
 * @Date 2022/2/10 17:37
 * @Author Admin
 * @Description
 */
@Data
@ApiModel
public class SearchMessageByIdForm {

    @NotBlank
    private String id;
}
