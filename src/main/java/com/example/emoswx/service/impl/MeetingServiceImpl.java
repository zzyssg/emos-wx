package com.example.emoswx.service.impl;


import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.ArrayUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.example.emoswx.common.util.R;
import com.example.emoswx.db.dao.TbMeetingDao;
import com.example.emoswx.db.dao.TbUserDao;
import com.example.emoswx.db.pojo.TbMeeting;
import com.example.emoswx.exception.EmosException;
import com.example.emoswx.service.MeetingService;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;

/**
* @author Admin
* @description 针对表【tb_meeting(会议表)】的数据库操作Service实现
* @createDate 2022-02-12 17:52:55
*/
@Service
@Slf4j
public class MeetingServiceImpl implements MeetingService {

    @Autowired
    TbMeetingDao meetingDao;

    @Autowired
    TbUserDao userDao;

    @Value("${emos.receiveNotify}")
    private String receiveNotify;

    @Value("${emos.code}")
    private String code;

    @Value("${workflow.url}")
    private String workflow;

    @Override
    public void insertMeeting(TbMeeting meeting) {
        int row =  meetingDao.insertMeeting(meeting);
        if (row != 1) {
            throw new EmosException("添加会议失败");
        }
        //TODO 开启工作流审批
        startMeetingWorkflow(meeting.getUuid(), meeting.getCreatorId().intValue(),
                meeting.getDate(), meeting.getStart());
    }

    private void startMeetingWorkflow(String uuid,int creatorId,String date,String start) {
        //查询用户信息，根据用户信息查询部门经理以及总经理id，
        HashMap userInfo = userDao.searchUserInfo(creatorId);
        JSONObject json = new JSONObject();
        json.set("uuid", uuid);
        json.set("url", receiveNotify);
        json.set("openId", userInfo.get("openId"));
        json.set("code", code);
        json.set("start", start);
        String[] roles = userInfo.get("roles").toString().split("，");
        //如果不是总经理创建的会议
        if (!ArrayUtil.contains(roles,"总经理")) {
            //将总经理和部门经理放进去
            int gmId = userDao.searchGmId();
            json.set("gmId", gmId);
            int deptManagerId = userDao.searchDeptManagerId((Integer) userInfo.get("deptId"));
            json.set("managerId", deptManagerId);
            //查询会议员工是不是同一个部门
            boolean inSameDept = meetingDao.searchMeetingMembersInSameDept(uuid);
            json.set("sameDept", inSameDept);
        }
        String url = workflow + "/workflow/startMeetingProcess";
        //请求工作流接口,开启工作流
        HttpResponse response = HttpRequest.post(url).header("Content-Type", "application/json").body(json.toString()).execute();
        if (response.getStatus() == 200) {
            json = JSONUtil.parseObj(response.body());
            //如果工作流创建成功，更新会议状态
            String instanceId = json.getStr("instanceId");
            HashMap param = new HashMap();
            param.put("instanceId", instanceId);
            param.put("uuid", uuid);
            int rows = meetingDao.updateInstanceId(param);//会议记录中保存工作流实例的id
            if (rows != 1) {
                throw new EmosException("保存会议工作流instanceId失败");
            }
        }
    }

    /*按照日期对所得的会议进行分组*/
    @Override
    public ArrayList<HashMap> searchMyMeetingListByPage(HashMap param) {
        ArrayList<HashMap> allMeetings = meetingDao.searchMyMeetingListByPage(param);
        ArrayList resultList = new ArrayList();
        String date = null;
        HashMap sameDateMeetings = null;
        JSONArray meetings = null;
        //日期已经是排好序的了
        for (HashMap meeting : allMeetings) {
            String meetDate = (String) meeting.get("date");
            if (!meetDate.equals(date)) {
                date = meetDate;
                //
                meetings = new JSONArray();
                sameDateMeetings = new HashMap();
                sameDateMeetings.put("date", date);
                sameDateMeetings.put("list", meetings);
                resultList.add(sameDateMeetings);
            }
            meetings.put(meeting);
        }
        return resultList;
    }

    @Override
    public HashMap searchMeetingById(int meetingId) {
        HashMap meetingMap = meetingDao.searchMeetingById(meetingId);
        ArrayList<HashMap> members = meetingDao.searchMeetingMembers(meetingId);
        meetingMap.put("members", members);
        return meetingMap;
    }

    @Override
    public void updateMeetingInfo(HashMap param) {
        //更新会议记录
        HashMap oldMeeting = meetingDao.searchMeetingById((Integer) param.get("id"));
        String start = oldMeeting.get("start").toString();
        String uuid = oldMeeting.get("uuid").toString();
        oldMeeting.get("start").toString();
        int creatorId = Integer.parseInt(oldMeeting.get("creatorId").toString());
        int rows = meetingDao.updateMeetingInfo(param);
        String date = oldMeeting.get("date").toString();
        if (rows != 1) {
            throw new EmosException("更新会议失败");
        }
        //删除旧的会议工作流
        JSONObject json = new JSONObject();
        json.set("instanceId", oldMeeting.get("instanceId"));
        json.set("reason", "更新会议");
        json.set("code", code);
        json.set("uuid", oldMeeting.get("uuid"));
        String delUrl = workflow + "/deleteProcessById";
        HttpResponse response = HttpRequest.post(delUrl).header("content-type", "application-json").body(json.toString()).execute();

        if (response.getStatus() != 200) {
            log.error("删除旧工作流失败");
            throw new EmosException("删除旧工作流失败");
        }

        //创建新的工作流
        startMeetingWorkflow(uuid,
                creatorId,
                date,
                start
        );

    }

    @Override
    public void deleteMeetingById(int id) {
        HashMap meeting = meetingDao.searchMeetingById(id);
        String start = (String) meeting.get("start");
        DateTime dateTime = DateUtil.parse(meeting.get("date") + " " + start);
        //会议开始前20min不能删除
        DateTime now = DateUtil.date();
        if (now.isAfter(DateUtil.offset(dateTime, DateField.MINUTE, -20))) {
            throw new EmosException("会议开始前20min不能删除");
        }
        //删除会议记录
        int row = meetingDao.deleteMeetingById(id);
        if (row != 1) {
            throw new EmosException("删除会议记录失败");
        }
        //删除会议工作流
        String delUrl = workflow + "/workflow/deleteProcessById";
        String instanceId = (String) meeting.get("instanceId");
        String uuid = (String) meeting.get("uuid");
        JSONObject json = new JSONObject();
        json.set("instanceId", instanceId);
        json.set("reason","删除工作流" );
        json.set("code",code );
        json.set("uuid", uuid);
        HttpResponse response = HttpRequest.post(delUrl).header("context-type", "application/json").body(json.toString()).execute();
        log.info("response:",response);
        if (response.getStatus() != 200) {
            throw new EmosException("删除工作流失败");
        }
    }

}




