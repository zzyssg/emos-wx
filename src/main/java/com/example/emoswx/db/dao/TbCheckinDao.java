package com.example.emoswx.db.dao;

import cn.hutool.core.lang.hash.Hash;
import com.example.emoswx.db.pojo.TbCheckin;
import org.apache.ibatis.annotations.Mapper;

import java.util.ArrayList;
import java.util.HashMap;

@Mapper
public interface TbCheckinDao {
    public Integer haveCheckin(HashMap map);

    public void insert(TbCheckin checkin);

    /*查询员工当天签到情况*/
    public HashMap searchTodayCheckin(int userId);

    /*查询签到天数*/
    public long searchCheckinDays(int userId);

    /*查询工作日考勤*/
    public ArrayList<HashMap> searchWeekCheckin(HashMap checkinMap);

}