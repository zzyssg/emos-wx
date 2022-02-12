package com.example.emoswx.controller.form;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @ClassName DeleteMessageRefByIdForm
 * @Date 2022/2/10 17:49
 * @Author Admin
 * @Description
 */
@Data
@ApiModel
public class DeleteMessageRefByIdForm {

    @NotBlank
    private String id;

}
