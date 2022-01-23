package com.example.emoswx.db.dao;

import com.example.emoswx.db.pojo.TbHolidays;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface TbHolidaysDao {
    public Integer searchTodayIsHoliday();
}