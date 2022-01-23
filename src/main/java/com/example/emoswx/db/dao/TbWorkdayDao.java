package com.example.emoswx.db.dao;

import com.example.emoswx.db.pojo.TbWorkday;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface TbWorkdayDao {
    public Integer searchTodayIsWorkday();
}