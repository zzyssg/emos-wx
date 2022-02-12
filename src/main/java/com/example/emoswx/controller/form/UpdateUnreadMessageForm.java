package com.example.emoswx.controller.form;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @ClassName UpdateUnreadMessageForm
 * @Date 2022/2/10 17:43
 * @Author Admin
 * @Description
 */
@Data
@ApiModel
public class UpdateUnreadMessageForm {

    @NotBlank
    private String id;
}
