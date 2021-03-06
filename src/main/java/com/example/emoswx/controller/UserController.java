package com.example.emoswx.controller;

import cn.hutool.json.JSONUtil;
import com.example.emoswx.common.util.R;
import com.example.emoswx.config.shiro.JwtUtil;
import com.example.emoswx.controller.form.*;
import com.example.emoswx.exception.EmosException;
import com.example.emoswx.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiOperation;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
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

    //用户登录
    @PostMapping("/login")
    @ApiOperation("登录系统")
    public R login(@Valid @RequestBody LoginForm loginForm) {
        //插入数据后获取用户的id
        int id = userService.longin(loginForm.getCode());
        //根据id获取token、permissions
        String token = jwtUtil.creatToken(id);
        Set<String> premissions = userService.searchUserPermissions(id);
        return R.ok("登录成功").put("token", token).put("premissions", premissions);
    }

    @PostMapping("/addUser")
    @ApiOperation("添加用户")
    @RequiresPermissions(value = {"ROOT", "USER:ADD"}, logical = Logical.OR)
    public R addUser() {
        return R.ok("添加用户成功！");
    }

    @GetMapping("/searchUserSummary")
    @ApiOperation("查询用户摘要")
    public R searchUserSummary(@RequestHeader("token") String token) {
        int userId = jwtUtil.getUserId(token);
        HashMap userSummary = userService.searchUserSummary(userId);
        return R.ok().put("result", userSummary);

    }

    @PostMapping("/searchUserGroupByDept")
    @RequiresPermissions(value = {"ROOT","EMPLOYEE:SELECT"},logical = Logical.OR)
    @ApiOperation("根据部门查询人员")
    public R searchUserGroupByDept(@Valid @RequestBody SearchUserGroupByDeptForm form) {
        ArrayList<HashMap> userGroupByDept = userService.searchUserGroupByDept(form.getKeyword());
        return R.ok().put("result", userGroupByDept);
    }

    @PostMapping("/searchMembers")
    @ApiOperation("查询成员")
    @RequiresPermissions(value = {"ROOT","MEETING:INSERT","MEETING:UPDATE"},logical = Logical.OR)
    public R searchMembers(@Valid @RequestBody SearchMembersForm form) {

        if (!JSONUtil.isJsonArray(form.getMembers())) {
            throw new EmosException("members不是json数组");
        }
        List<List> mermbers = JSONUtil.parseArray(form.getMembers()).toList(List.class);
        ArrayList<HashMap> members = userService.searchMembers(mermbers);
        return R.ok().put("result", members);
    }

    @PostMapping("/searchUserPhotoAndName")
    @ApiOperation("查询用户的名字和头像")
    @RequiresPermissions(value = {"WORKFLOW:APPROVAL"}, logical = Logical.OR)
    public R searchUserPhotoAndName(@Valid @RequestBody SelectUserPhotoAndNameForm form) {
        if (!JSONUtil.isJsonArray(form.getIds())) {
            throw new EmosException("参数不是json数组");
        }
        List<Integer> list = JSONUtil.parseArray(form.getIds()).toList(Integer.class);
        ArrayList<HashMap> result = userService.selectUserPhotoAndName(list);
        return R.ok().put("result", result);
    }
}
