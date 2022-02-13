package com.example.emoswx.service.impl;


import cn.hutool.json.JSONArray;
import com.example.emoswx.db.dao.TbMeetingDao;
import com.example.emoswx.db.pojo.TbMeeting;
import com.example.emoswx.exception.EmosException;
import com.example.emoswx.service.MeetingService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;

/**
* @author Admin
* @description 针对表【tb_meeting(会议表)】的数据库操作Service实现
* @createDate 2022-02-12 17:52:55
*/
@Service
public class MeetingServiceImpl implements MeetingService {

    @Autowired
    TbMeetingDao meetingDao;

    @Override
    public void insertMeeting(TbMeeting meeting) {
        int row =  meetingDao.insertMeeting(meeting);
        if (row != 1) {
            throw new EmosException("添加会议失败");
        }
        //TODO 开启工作流审批
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
}




