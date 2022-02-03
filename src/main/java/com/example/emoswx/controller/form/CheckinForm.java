package com.example.emoswx.controller.form;

import io.swagger.annotations.ApiModel;
import lombok.Data;

/**
 * @author ZZy
 * @date 2022/2/1 13:15
 * @description
 */
@Data
@ApiModel
public class CheckinForm {
    private String address;
    private String country;
    private String province;
    private String city;
    private String district;

}
