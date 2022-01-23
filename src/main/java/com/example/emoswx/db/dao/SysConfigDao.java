package com.example.emoswx.db.dao;

import com.example.emoswx.db.pojo.SysConfig;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface SysConfigDao {

    public List<SysConfig> selectAllParams();
}