package com.example.emoswx.db.dao;

import com.example.emoswx.db.pojo.TbFaceModel;
import org.apache.ibatis.annotations.Mapper;

@Mapper
public interface TbFaceModelDao {

    public String searchFaceModel(int userId);

    public void insert(TbFaceModel faceModel);

    public int deleteFaceModel(int userId);
}