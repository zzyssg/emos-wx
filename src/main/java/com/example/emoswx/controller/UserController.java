package com.example.emoswx.controller;

import com.example.emoswx.common.util.R;
import com.example.emoswx.config.shiro.JwtUtil;
import com.example.emoswx.controller.form.RegisterForm;
import com.example.emoswx.service.UserService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * @ClassName UserController
 * @Date 2022/1/19 0:08
 * @Author Admin
 * @Description
 */
@RestController
@RequestMapping("/user")
@Api("用户模块接口")
public class UserController {

    @Value("${emos.jwt.cache-expire}")
    private int cacheExpire;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private UserService userService;

    @PostMapping("/register")
    public R register(@Valid @RequestBody RegisterForm form) {
        //查询是否已经注册，若没有则注册
        int id = userService.registerUser(form.getRegisterCode(), form.getCode(), form.getPhoto(), form.getNickname());
        //根据id值创建token
        String token = jwtUtil.creatToken(id);
        //查询用户权限
        Set<String> permissions = userService.searchUserPermissions(id);
        //redis存token
        saveCacheToken(token, id);
        return R.ok("用户注册成功").put("token", token).put("permissions", permissions);

    }

    //redis存储token
    private void saveCacheToken(String token, int userId) {
        redisTemplate.opsForValue().set(token, userId + "", cacheExpire, TimeUnit.DAYS);
    }

}
