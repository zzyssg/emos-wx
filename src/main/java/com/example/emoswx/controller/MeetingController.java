package com.example.emoswx.controller;

import cn.hutool.json.JSONUtil;
import com.example.emoswx.common.util.R;
import com.example.emoswx.config.shiro.JwtUtil;
import com.example.emoswx.controller.form.SearchMembersForm;
import com.example.emoswx.controller.form.SearchMyMeetingListByPageForm;
import com.example.emoswx.exception.EmosException;
import com.example.emoswx.service.MeetingService;
import com.example.emoswx.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.apache.catalina.User;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * @ClassName MeetingController
 * @Date 2022/2/12 19:02
 * @Author Admin
 * @Description
 */
@RestController
@RequestMapping("/meeting")
@Api("会议接口")
public class MeetingController {

    @Autowired
    private MeetingService meetingService;

    @Autowired
    UserService userService;

    @Autowired
    JwtUtil jwtUtil;

    @PostMapping("/searchMyMeetingListByPage")
    @ApiOperation("分页查询会议")
    public R searchMyMeetingListByPage(@Valid @RequestBody SearchMyMeetingListByPageForm form, @RequestHeader String token) {
        int userId = jwtUtil.getUserId(token);
        int page = form.getPage();
        int length = form.getLength();
        int start = (page - 1) * length;

        HashMap param = new HashMap();

        param.put("start", start);
        param.put("length", length);
        param.put("userId", userId);
        ArrayList<HashMap> meetingsByPage = meetingService.searchMyMeetingListByPage(param);
        return R.ok().put("result", meetingsByPage);
    }



}
