package com.example.emoswx.controller.form;

import io.swagger.annotations.ApiModel;
import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * @ClassName ReceiveNotifyForm
 * @Date 2022/2/15 22:41
 * @Author Admin
 * @Description
 */
@Data
@ApiModel
public class ReceiveNotifyForm {
    @NotBlank
    private String processId;
    @NotBlank
    private String uuid;
    @NotBlank
    private String result;
}

