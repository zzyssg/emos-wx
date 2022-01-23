package com.example.emoswx.controller.form;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import javax.validation.Valid;
import javax.validation.constraints.NotBlank;

/**
 * @ClassName LoginForm
 * @Date 2022/1/19 21:12
 * @Author Admin
 * @Description
 */
@Data
@ApiModel
public class LoginForm {

    @NotBlank(message = "临时授权码不能为空")
    private String code;
}
