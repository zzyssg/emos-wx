package com.example.emoswx.db.dao;

import com.example.emoswx.db.pojo.TbCity;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface TbCityDao {

    public String searchCode(String city);

}