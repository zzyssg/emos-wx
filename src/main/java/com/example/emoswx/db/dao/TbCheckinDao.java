package com.example.emoswx.db.dao;

import com.example.emoswx.db.pojo.TbCheckin;
import org.apache.ibatis.annotations.Mapper;

import java.util.HashMap;

@Mapper
public interface TbCheckinDao {
    public Integer haveCheckin(HashMap map);

    public void insert(TbCheckin checkin);
}