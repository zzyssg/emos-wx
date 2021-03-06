package com.example.emoswx.db.dao;

import com.example.emoswx.db.pojo.TbUser;
import org.apache.ibatis.annotations.Mapper;

import java.util.*;

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

    TbUser searchById(int userId);

    public HashMap searchNameAndDept(int userId);

    /*查询员工入职日期*/
    public String searchHiredate(int userId);

    /*查询用户摘要*/
    public HashMap searchUserSummary(int userId);

    /*根据部门查询用户部门*/
    public ArrayList<HashMap> searchUserGroupByDept(String keyword);

    /*查询成员*/
    public ArrayList<HashMap> searchMembers(List param);

    public HashMap searchUserInfo(int userId);

    public int searchDeptManagerId(int id);

    public int searchGmId();

    public ArrayList<HashMap> selectUserPhotoAndName(List list);



}
