package com.example.emoswx.db.dao;


import cn.hutool.core.lang.hash.Hash;
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
}




