package com.example.emoswx.db.dao;

import org.apache.ibatis.annotations.Mapper;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * @ClassName TbUserDao
 * @Date 2022/1/18 13:39
 * @Author Admin
 * @Description
 */
@Mapper
public interface TbUserDao {
    public boolean haveRootUser();

    //TODO ？为什么没有加泛型
    public int insert(HashMap params);

    //查询用户主键id
    public Integer searchIdByOpenid(String openId);

    //查询用户查询
    public Set<String> searchUserPermissions(int userId);
}
