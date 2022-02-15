package com.example.emoswx.controller;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.json.JSONUtil;
import com.example.emoswx.common.util.R;
import com.example.emoswx.config.shiro.JwtUtil;
import com.example.emoswx.controller.form.*;
import com.example.emoswx.db.pojo.TbMeeting;
import com.example.emoswx.exception.EmosException;
import com.example.emoswx.service.MeetingService;
import com.example.emoswx.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.Logical;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;

/**
 * @ClassName MeetingController
 * @Date 2022/2/12 19:02
 * @Author Admin
 * @Description
 */
@RestController
@RequestMapping("/meeting")
@Api("会议接口")
@Slf4j
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

    @PostMapping("/insertMeeting")
    @ApiOperation("添加会议")
    public R insertMeeting(@Valid @RequestBody InsertMeetingForm form, @RequestHeader("token") String token) {
        //对字段进行检查
        if (form.getType() == 2 && (form.getPlace() == null || form.getPlace() == "")) {
            throw new EmosException("线下会议地点不能为空");
        }
        DateTime startTime = DateUtil.parse(form.getDate() + " " + form.getStart() + ":00");
        DateTime endTime = DateUtil.parse(form.getDate() + " " + form.getEnd() + ":00");
        if (endTime.isBefore(startTime)) {
            throw new EmosException("开始时间不能晚于结束时间");
        }
        if (!JSONUtil.isJson(form.getMembers())) {
            throw new EmosException("members不是json数组");
        }

        //插入会议数据
        TbMeeting meeting = new TbMeeting();
        meeting.setCreateTime(new Date());
        meeting.setDate(form.getDate());
        meeting.setStatus((short) 1);
        meeting.setCreatorId((long) jwtUtil.getUserId(token));
        meeting.setDesc(form.getDesc());
        meeting.setEnd(form.getEnd());
        meeting.setStart(form.getStart());
        meeting.setMembers(form.getMembers());
        meeting.setPlace(form.getPlace());
        meeting.setTitle(form.getTitle());
        meeting.setUuid(IdUtil.randomUUID()); //创建会议记录时生成uuid
        meeting.setType((short) form.getType());
        meetingService.insertMeeting(meeting);
        return R.ok().put("result", "success");
    }


    @PostMapping("/searchMeetingByIdForm")
    @ApiOperation("根据会议id查询会议")
    @RequiresPermissions(value = {"ROOT","MEETING:SELECT"},logical = Logical.AND)
    public R searchMeetingByIdForm(@Valid @RequestBody SearchMeetingByIdForm form, @RequestHeader("token") String token) {
        HashMap meeting = meetingService.searchMeetingById(form.getId());
        return R.ok().put("result", meeting);

    }

    @PostMapping("/updateMeetingInfo")
    @ApiOperation("更新会议信息")
    @RequiresPermissions(value = {"ROOT", "MEETING:UPDATE"}, logical = Logical.OR)
    public R updateMeetingInfo(@Valid @RequestBody UpdateMeetingInfoForm form, @RequestHeader("token") String token) {
        //对字段进行检查
        if (form.getType() == 2 && (form.getPlace() == null || form.getPlace() == "")) {
            throw new EmosException("线下会议地点不能为空");
        }
        DateTime startTime = DateUtil.parse(form.getDate() + " " + form.getStart() + ":00");
        DateTime endTime = DateUtil.parse(form.getDate() + " " + form.getEnd() + ":00");
        if (endTime.isBefore(startTime)) {
            throw new EmosException("开始时间不能晚于结束时间");
        }
        if (!JSONUtil.isJson(form.getMembers())) {
            throw new EmosException("members不是json数组");
        }

        //更新会议

        HashMap param = new HashMap();
        param.put("title",form.getTitle());
        param.put("date",form.getDate());
        param.put("place",form.getPlace());
        param.put("start",form.getStart());
        param.put("end",form.getEnd());
        param.put("type",form.getType());
        param.put("members",form.getMembers());
        param.put("desc",form.getDesc());
        param.put("id",form.getId());
        param.put("instanceId",form.getInstanceId());
        //会议状态
        param.put("status",1);
        meetingService.updateMeetingInfo(param);
        return R.ok().put("result", "success");
    }

    @PostMapping("/deleteMeetingById")
    @ApiOperation("根据会议id删除会议")
//    @RequiresPermissions(value = {"ROOT", "MEETING:DELETE"}, logical = Logical.OR)
    public R deleteMeetingById(@Valid @RequestBody DeleteMeetingByIdForm form) {
        meetingService.deleteMeetingById(form.getId());
        return R.ok().put("result", "success");
    }

    @PostMapping("/receiveNotify")
    @ApiOperation("接收工作流通知")
    public R receiveNotify(@Valid @RequestBody ReceiveNotifyForm form) {
        if ("同意".equals(form.getResult())) {
            log.debug(form.getUuid() + "的会议审批通过");
        } else {
            log.debug(form.getUuid() + "的会议审批未通过");
        }
        return R.ok();
    }



}
