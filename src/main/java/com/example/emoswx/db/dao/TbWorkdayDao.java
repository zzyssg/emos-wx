package com.example.emoswx.db.dao;

import com.example.emoswx.db.pojo.TbWorkday;
import org.apache.ibatis.annotations.Mapper;

import java.util.ArrayList;
import java.util.HashMap;

@Mapper
public interface TbWorkdayDao {
    public Integer searchTodayIsWorkday();

    /*查询日期范围内的工作日期*/
    public ArrayList<String> searchWorkDayInRange(HashMap dateRangeMa);
}