package com.example.emoswx.controller.form;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

/**
 * @ClassName SearchMyMeetingListByPageForm
 * @Date 2022/2/12 19:00
 * @Author Admin
 * @Description
 */
@Data
@ApiModel
public class SearchMyMeetingListByPageForm {

    @NotNull
    @Min(1)
    private Integer page;

    @NotNull
    @Range(min = 1,max = 40)
    private Integer length;

}
