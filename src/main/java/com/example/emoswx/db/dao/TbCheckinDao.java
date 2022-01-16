package com.example.emoswx.db.dao;

import com.example.emoswx.db.pojo.TbCheckin;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface TbCheckinDao {
    int deleteByPrimaryKey(Integer id);

    int insert(TbCheckin record);

    int insertSelective(TbCheckin record);

    TbCheckin selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(TbCheckin record);

    int updateByPrimaryKey(TbCheckin record);
}