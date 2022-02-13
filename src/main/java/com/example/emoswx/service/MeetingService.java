package com.example.emoswx.service;

import com.example.emoswx.db.pojo.TbMeeting;

import java.util.ArrayList;
import java.util.HashMap;

/**
* @author Admin
* @description 针对表【tb_meeting(会议表)】的数据库操作Service
* @createDate 2022-02-12 17:52:55
*/
public interface MeetingService {
    public void insertMeeting(TbMeeting meeting);

    public ArrayList<HashMap> searchMyMeetingListByPage(HashMap param);

}
