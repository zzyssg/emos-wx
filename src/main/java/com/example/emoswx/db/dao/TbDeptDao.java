package com.example.emoswx.db.dao;

import com.example.emoswx.db.pojo.TbDept;
import org.apache.ibatis.annotations.Mapper;

import java.util.ArrayList;
import java.util.HashMap;

@Mapper
public interface TbDeptDao {

    public ArrayList<HashMap> searchDeptMembers(String keyword);
}