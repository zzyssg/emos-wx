package com.example.emoswx.service.impl;

import cn.hutool.core.util.IdUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSON;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.example.emoswx.db.dao.TbUserDao;
import com.example.emoswx.db.pojo.MessageEntity;
import com.example.emoswx.db.pojo.TbUser;
import com.example.emoswx.exception.EmosException;
import com.example.emoswx.service.UserService;
import com.example.emoswx.task.MessageTask;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Service;

import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.util.Date;
import java.util.HashMap;
import java.util.Set;

/**
 * @ClassName UserServiceImpl
 * @Date 2022/1/18 19:12
 * @Author Admin
 * @Description
 */
@Slf4j
@Service
@Scope("prototype")
public class UserServiceImpl implements UserService {


    @Value("${wx.app-id}")
    private String appid;

    @Value("${wx.app-secret}")
    private String appSecret;

    @Autowired
    private TbUserDao userDao;

    @Autowired
    private MessageTask messageTask;


    //    根据临时授权字符串，向微信获取openId
    private String getOpenId(String code) {
        String url = "https://api.weixin.qq.com/sns/jscode2session";
        HashMap map = new HashMap();
        map.put("appid", appid);
        map.put("secret", appSecret);
        map.put("js_code", code);
        map.put("grant_type", "authorization_code");
        //发送请求，获取response
        String response = HttpUtil.post(url, map);
        JSONObject respJO = JSONUtil.parseObj(response);
        String openId = respJO.getStr("openid");
        if (openId == null || openId.length() == 0) {
            throw new RuntimeException("临时登录凭证错误");
        }
        return openId;

    }

    @Override
    public int registerUser(String registerCode, String code, String photo, String nickname) {
        //先处理系统管理员
        if ("000000".equals(registerCode)) {
            //查看是否已经存在系统管理员
            boolean haveRootUser = userDao.haveRootUser();
            if (!haveRootUser) {
                //设置系统管理员

                HashMap rootMap = new HashMap();
                String openId = getOpenId(code);
                rootMap.put("openId", openId);
                rootMap.put("photo", photo);
                rootMap.put("nickname", nickname);
                rootMap.put("createTime", new Date());
                rootMap.put("root", 1);
                rootMap.put("status", 1);
                rootMap.put("role", "[0]");
                userDao.insert(rootMap);

                int id = userDao.searchIdByOpenid(openId);
                //发送系统消息，添加成功
                MessageEntity entity = new MessageEntity();
                entity.setSendTime(new Date());
                entity.setMsg("你已注册为超级管理员，请及时更新个人信息");
                entity.setSenderId(0);
                entity.setSenderName("系统消息");
                entity.setUuid(IdUtil.simpleUUID());
                messageTask.sendAsync(id + "", entity);

                return id;
            } else {
                throw new EmosException("无法绑定超级管理员");
            }
        }
        //TODO 除了管理员的其他判断情况
        else {
            return 0;
        }

    }

    @Override
    public Set<String> searchUserPermissions(int userId) {
        return userDao.searchUserPermissions(userId);
    }

    @Override
    public int longin(String code) {
        //code - openid - 存在或者不存在
        String openId = getOpenId(code);
        Integer id = userDao.searchIdByOpenid(openId);
        if (id == null) {
            throw new EmosException("账户不存在");
        }
        //从消息队列中取消息
        messageTask.receiveAsync(id + "");

        return id;
    }

    @Override
    public TbUser searchById(int userId) {
        return userDao.searchById(userId);
    }

    @Override
    public String searchHiredate(int userId) {
        return userDao.searchHiredate(userId);
    }

    @Override
    public HashMap searchUserSummary(int userId) {
        return userDao.searchUserSummary(userId);
    }
}
