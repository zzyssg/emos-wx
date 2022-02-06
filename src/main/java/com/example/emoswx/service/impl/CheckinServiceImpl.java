package com.example.emoswx.service.impl;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.http.HttpRequest;
import cn.hutool.http.HttpResponse;
import cn.hutool.http.HttpUtil;
import com.example.emoswx.config.SysConstant;
import com.example.emoswx.db.dao.*;
import com.example.emoswx.db.pojo.TbCheckin;
import com.example.emoswx.db.pojo.TbFaceModel;
import com.example.emoswx.exception.EmosException;
import com.example.emoswx.service.CheckinService;
import com.example.emoswx.task.EmailTask;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Scope;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;

/**
 * @ClassName CheckinServiceImpl
 * @Date 2022/1/23 17:35
 * @Author Admin
 * @Description
 */
@Service
@Slf4j
@Scope("prototype")
public class CheckinServiceImpl implements CheckinService {

    @Autowired
    SysConstant sysConstant;

    @Autowired
    TbHolidaysDao holidaysDao;

    @Autowired
    TbWorkdayDao workdayDao;

    @Autowired
    TbCheckinDao checkinDao;

    @Autowired
    TbFaceModelDao faceModelDao;

    @Autowired
    TbCityDao cityDao;

    @Autowired
    TbUserDao userDao;

    @Value("emos.email.hr")
    private String hrEmail;

    @Autowired
    private EmailTask emailTask;

    @Value("${emos.face.checkinUrl}")
    private String checkinUrl;

    @Value("${emos.code}")
    private String code;

    @Value("${emos.face.createFaceModelUrl}")
    private String createFaceModelUrl;

    @Override
    public String validCanCheckin(int userId,String date) {
        boolean isWorkday = workdayDao.searchTodayIsWorkday() != null ? true : false;
        boolean isHoliday = holidaysDao.searchTodayIsHoliday() != null ? true : false;
        String dayType = "工作日";
        if (DateUtil.date().isWeekend()) {
            dayType = "节假日";
        }
        //排除特殊情况
        if (isHoliday) {
            dayType = "节假日";
        } else if (isWorkday) {
            dayType = "工作日";
        }
        if ("节假日".equals(dayType)) {
            return "节假日，无须打卡";
        } else {
            //TODO date和today的区别
            DateTime now = DateUtil.date();
            String start = DateUtil.today() + " " + sysConstant.getAttendanceStartTime();
            String end = DateUtil.today() + " " + sysConstant.getAttendanceEndTime();
            DateTime attendStart = DateUtil.parse(start);
            DateTime attendEnd = DateUtil.parse(end);
            if (now.isBefore(attendStart)) {
                return "还未到上班打卡时间";
            } else if (now.isAfter(attendEnd)) {
                return "上班打卡时间已结束";
            } else {
                //可以打卡，打卡人员信息
                HashMap map = new HashMap();
                map.put("userId", userId);
                map.put("start",start);
                map.put("end",end);
                map.put("date", date);
                boolean haveCheckined = checkinDao.haveCheckin(map) != null ? true : false;
                return haveCheckined ? "上班打卡已完成，请勿重复打卡" : "可以打卡";
            }

        }


    }

    @Override
    public void checkin(HashMap checkinMap) {
        //判断签到
        Date d1 = DateUtil.date();
        Date d2 = DateUtil.parse(DateUtil.today() + " " + sysConstant.attendanceStartTime);
        Date d3 = DateUtil.parse(DateUtil.today() + " " + sysConstant.attendanceEndTime);
        //1为正常签到，2为迟到
        int status = 1;
        if (d1.compareTo(d2) < 0) {
            status = 1;
        } else if (d1.compareTo(d2) > 0 && d1.compareTo(d3) < 0) {
            status = 2;
        }
        //查询签到人的人脸模型
        int userId = (int) checkinMap.get("userId");
        String faceModel = faceModelDao.searchFaceModel(userId);
        if (faceModel == null) {
            throw new EmosException("不存在人脸模型");
        } else {
            String path = (String) checkinMap.get("path");
            HttpRequest request = HttpUtil.createPost(checkinUrl);
            request.form("photo", FileUtil.file(path), "targetModel", faceModel);
            request.form("code",code);
            HttpResponse response = request.execute();
            String body = response.body();
            if ("无法识别出人脸".equals(body) || "照片中存在多张人脸".equals(body)) {
                throw new EmosException(body);
            } else if ("False".equals(body)) {
                throw new EmosException("签到无效，非本人签到");
            } else if ("True".equals(body)) {
                //TODO 获取签到疫情风险等级
                int risk = 1;
                String city = (String) checkinMap.get("city");
                String district = (String) checkinMap.get("district");
                if (!StrUtil.isBlank(city) && !StrUtil.isBlank(district)) {
                    String code = cityDao.searchCode(city);
                    String url = "http://m." + code + ".bendibao.com/news/yqdengji/?qu=" + district;
                    try {
                        Document doc = Jsoup.connect(url).get();
                        Elements elements = doc.getElementsByClass("list-detail");
                        for (Element e : elements) {
                            String res = e.text().split(" ")[1];
                            if ("高风险".equals(res)) {
                                risk = 3;
                                //发送告警邮件
                                HashMap emp = userDao.searchNameAndDept(userId);
                                String name = (String) emp.get("name");
                                String deptName = (String) emp.get("deptName");
                                deptName = deptName == null ? "" : deptName;
                                SimpleMailMessage message = new SimpleMailMessage();
                                message.setTo(hrEmail);
                                message.setSubject("员工" + name + "身处高风险疫情区警告");
                                message.setText(deptName + "员工" + name + "," + DateUtil.format(new Date(),"yyyy-mm-dd") +
                                        "处于" + "请及时与该员工联系，核实情况");
                                emailTask.sendAsync(message);
                            } else if ("中风险".equals(res)) {
                                risk = risk < 2 ? 2 : risk;
                            }
                        }
                    } catch (IOException e) {
                        log.error("执行异常", e);
                        throw new EmosException("获取风险等级失败");
                    }
                }
                //TODO 保存到签到表中
                TbCheckin entity = new TbCheckin();
                entity.setUserId(userId);
                entity.setCity(city);
                entity.setDate(DateUtil.today());
                entity.setDistrict(district);
                entity.setCreateTime(d1);
                entity.setRisk(risk);
                entity.setStatus((byte) status);
                checkinDao.insert(entity);
            }
        }

    }

    @Override
    public void createFaceModel(int userId, String faceModel) {
        HttpRequest request = HttpUtil.createPost(createFaceModelUrl);
        request.form("photo", FileUtil.file(faceModel));
        request.form("code",code);
        HttpResponse response = request.execute();
        String body = response.body();
        if ("无法识别出人脸".equals(body) || "照片中存在多张人脸".equals(body)) {
            throw new EmosException(body);
        } else {
            TbFaceModel entity = new TbFaceModel();
            entity.setUserId(userId);
            entity.setFaceModel(body);
            faceModelDao.insert(entity);
        }


    }
}
