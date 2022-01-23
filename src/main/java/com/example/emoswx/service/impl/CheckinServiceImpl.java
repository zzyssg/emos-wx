package com.example.emoswx.service.impl;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import com.example.emoswx.config.SysConstant;
import com.example.emoswx.db.dao.TbCheckinDao;
import com.example.emoswx.db.dao.TbHolidaysDao;
import com.example.emoswx.db.dao.TbWorkdayDao;
import com.example.emoswx.service.CheckinService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.util.HashMap;

/**
 * @ClassName CheckinServiceImpl
 * @Date 2022/1/23 17:35
 * @Author Admin
 * @Description
 */
@Service
@Slf4j
@Scope("prototype")
public class CheckinServiceImpl implements CheckinService {

    @Autowired
    SysConstant sysConstant;

    @Autowired
    TbHolidaysDao holidaysDao;

    @Autowired
    TbWorkdayDao workdayDao;

    @Autowired
    TbCheckinDao checkinDao;

    @Override
    public String validCanCheckin(int userId,String date) {
        boolean isWorkday = workdayDao.searchTodayIsWorkday() != null ? true : false;
        boolean isHoliday = holidaysDao.searchTodayIsHoliday() != null ? true : false;
        String dayType = "工作日";
        if (DateUtil.date().isWeekend()) {
            dayType = "节假日";
        }
        //排除特殊情况
        if (isHoliday) {
            dayType = "节假日";
        } else if (isWorkday) {
            dayType = "工作日";
        }
        if ("节假日".equals(dayType)) {
            return "节假日，无须打卡";
        } else {
            //TODO date和today的区别
            DateTime now = DateUtil.date();
            String start = DateUtil.today() + " " + sysConstant.getAttendanceStartTime();
            String end = DateUtil.today() + " " + sysConstant.getAttendanceEndTime();
            DateTime attendStart = DateUtil.parse(start);
            DateTime attendEnd = DateUtil.parse(end);
            if (now.isBefore(attendStart)) {
                return "还未到上班打卡时间";
            } else if (now.isAfter(attendEnd)) {
                return "上班打卡时间已结束";
            } else {
                //可以打卡，打卡人员信息
                HashMap map = new HashMap();
                map.put("userId", userId);
                map.put("start",start);
                map.put("end",end);
                map.put("date", date);
                boolean haveCheckined = checkinDao.haveCheckin(map) != null ? true : false;
                return haveCheckined ? "上班打卡已完成，请勿重复打卡" : "可以打卡";
            }

        }


    }
}
