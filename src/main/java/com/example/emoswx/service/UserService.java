package com.example.emoswx.service;


import com.example.emoswx.db.pojo.TbUser;

import java.util.Set;

/**
* @author Admin
* @description 针对表【tb_user(用户表)】的数据库操作Service
* @createDate 2022-01-18 12:17:12
*/
public interface UserService {

    //注册成功，返回其openid
    public int registerUser(String registerCode,String code,String photo,String nickname);

    //查询用户的权限
    public Set<String> searchUserPermissions(int userId);
}
