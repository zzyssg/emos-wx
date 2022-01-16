package com.example.emoswx.controller;

import com.example.emoswx.common.util.R;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;

/**
 * @author ZZy
 * @date 2022/1/8 13:45
 * @description
 */


@RestController
@RequestMapping("/test")
@Api("测试第一个web接口")
public class TestController {

    @PostMapping("/sayHello")
    @ApiOperation("测试第一个方法")
    public R sayHello(@Valid @RequestBody TestControllerForm form) {
        System.out.println(form.getName());
        return new R().put("msg", "hello," + form.getName());
    }

}
