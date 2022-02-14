package com.example.emoswx.db.dao;


import com.example.emoswx.db.pojo.TbMeeting;
import org.apache.ibatis.annotations.Mapper;

import java.util.ArrayList;
import java.util.HashMap;

/**
* @author Admin
* @description 针对表【tb_meeting(会议表)】的数据库操作Mapper
* @createDate 2022-02-12 17:56:26
* @Entity com.example.emoswx.db.pojo.TbMeeting
*/
@Mapper
public interface TbMeetingDao {
    public int insertMeeting(TbMeeting meeting);

    public ArrayList<HashMap> searchMyMeetingListByPage(HashMap param);


    public boolean searchMeetingMembersInSameDept(String uuid);

    public int updateInstanceId(HashMap param);

    public HashMap searchMeetingById(int meetingId);

    public ArrayList<HashMap> searchMeetingMembers(int meetingId);

    public int updateMeetingInfo(HashMap param);

    public int deleteMeetingById(int id);

}




