package com.example.emoswx.db.dao;

import com.example.emoswx.db.pojo.TbHolidays;
import org.apache.ibatis.annotations.Mapper;

import java.util.ArrayList;
import java.util.HashMap;

@Mapper
public interface TbHolidaysDao {
    public Integer searchTodayIsHoliday();

    /*查询范围内的假期*/
    public ArrayList<String> searchHolidaysInrage(HashMap dateRangeMap);
}