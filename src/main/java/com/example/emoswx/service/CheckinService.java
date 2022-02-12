package com.example.emoswx.service;

import cn.hutool.core.lang.hash.Hash;

import java.util.ArrayList;
import java.util.HashMap;

/**
 * @ClassName CheckinService
 * @Date 2022/1/23 17:34
 * @Author Admin
 * @Description
 */
public interface CheckinService {
    public String validCanCheckin(int userId,String date);

    public void checkin(HashMap checkinMap);

    public void createFaceModel(int userId, String faceModel);

    /*查询员工当天签到情况*/
    public HashMap searchTodayCheckin(int userId);

    /*查询签到天数*/
    public long searchCheckinDays(int userId);

    /*查询工作日考勤*/
    public ArrayList<HashMap> searchWeekCheckin(HashMap checkinMap);

    /*查询月度考勤*/
    public ArrayList<HashMap> searchMonthCheckin(HashMap checkinMap);


}
