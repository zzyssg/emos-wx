package com.example.emoswx.controller.form;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @ClassName SelectUserPhotoAndNameForm
 * @Date 2022/2/15 22:54
 * @Author Admin
 * @Description
 */
@Data
@ApiModel
public class SelectUserPhotoAndNameForm {

    @NotBlank
    private String ids;
}
