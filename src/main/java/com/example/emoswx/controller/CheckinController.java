package com.example.emoswx.controller;

import cn.hutool.core.date.DateUtil;
import com.example.emoswx.common.util.R;
import com.example.emoswx.config.shiro.JwtUtil;
import com.example.emoswx.service.CheckinService;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @ClassName CheckinController
 * @Date 2022/1/23 18:00
 * @Author Admin
 * @Description
 */
@RestController
@RequestMapping("/checkin")
@Slf4j
@Api("签到模块接口")
public class CheckinController {

    @Autowired
    private JwtUtil jwtUtil;

    private CheckinService checkinService;

    public R validCanCheckin(@RequestHeader("token") String token) {
        int userId = jwtUtil.getUserId(token);
        String res = checkinService.validCanCheckin(userId, DateUtil.today());
        return R.ok(res);
    }

}
