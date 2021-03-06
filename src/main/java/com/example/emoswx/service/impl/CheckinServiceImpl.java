package com.example.emoswx.service.impl;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateRange;
import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.hash.Hash;
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
import org.springframework.expression.spel.ast.NullLiteral;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
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

    @Value("${emos.email.hr}")
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
        String dayType = "?????????";
        if (DateUtil.date().isWeekend()) {
            dayType = "?????????";
        }
        //??????????????????
        if (isHoliday) {
            dayType = "?????????";
        } else if (isWorkday) {
            dayType = "?????????";
        }
        if ("?????????".equals(dayType)) {
            return "????????????????????????";
        } else {
            //TODO date???today?????????
            DateTime now = DateUtil.date();
            String start = DateUtil.today() + " " + sysConstant.getAttendanceStartTime();
            String end = DateUtil.today() + " " + sysConstant.getAttendanceEndTime();
            DateTime attendStart = DateUtil.parse(start);
            DateTime attendEnd = DateUtil.parse(end);
            if (now.isBefore(attendStart)) {
                return "???????????????????????????";
            } else if (now.isAfter(attendEnd)) {
                return "???????????????????????????";
            } else {
                //?????????????????????????????????
                HashMap map = new HashMap();
                map.put("userId", userId);
                map.put("start",start);
                map.put("end",end);
                map.put("date", date);
                boolean haveCheckined = checkinDao.haveCheckin(map) != null ? true : false;
                return haveCheckined ? "??????????????????????????????????????????" : "????????????";
            }

        }


    }

    @Override
    public void checkin(HashMap checkinMap) {
        //????????????
        Date d1 = DateUtil.date();
        Date d2 = DateUtil.parse(DateUtil.today() + " " + sysConstant.attendanceStartTime);
        Date d3 = DateUtil.parse(DateUtil.today() + " " + sysConstant.attendanceEndTime);
        //1??????????????????2?????????
        int status = 1;
        if (d1.compareTo(d2) < 0) {
            status = 1;
        } else if (d1.compareTo(d2) > 0 && d1.compareTo(d3) < 0) {
            status = 2;
        }
        //??????????????????????????????
        int userId = (int) checkinMap.get("userId");
        String faceModel = faceModelDao.searchFaceModel(userId);
        if (faceModel == null) {
            throw new EmosException("?????????????????????");
        } else {
            String path = (String) checkinMap.get("path");
            HttpRequest request = HttpUtil.createPost(checkinUrl);
            request.form("photo", FileUtil.file(path), "targetModel", faceModel);
            request.form("code",code);
            HttpResponse response = request.execute();
            String body = response.body();
            if ("?????????????????????".equals(body) || "???????????????????????????".equals(body)) {
                throw new EmosException(body);
            } else if ("False".equals(body)) {
                throw new EmosException("??????????????????????????????");
            } else if ("True".equals(body)) {
                //TODO ??????????????????????????????
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
                            if ("?????????".equals(res)) {
                                risk = 3;
                                //??????????????????
                                HashMap emp = userDao.searchNameAndDept(userId);
                                String name = (String) emp.get("name");
                                String deptName = (String) emp.get("deptName");
                                deptName = deptName == null ? "" : deptName;
                                SimpleMailMessage message = new SimpleMailMessage();
                                message.setTo(hrEmail);

                                message.setSubject("??????" + name + "??????????????????????????????");
                                message.setText(deptName + "??????" + name + "," + DateUtil.format(new Date(),"yyyy-mm-dd") +
                                        "??????" + district + ",??????????????????????????????????????????");
                                emailTask.sendAsync(message);
                            } else if ("?????????".equals(res)) {
                                risk = risk < 2 ? 2 : risk;
                            }
                        }
                    } catch (IOException e) {
                        log.error("????????????", e);
                        throw new EmosException("????????????????????????");
                    }
                }
                //TODO ?????????????????????
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
        if ("?????????????????????".equals(body) || "???????????????????????????".equals(body)) {
            throw new EmosException(body);
        } else {
            TbFaceModel entity = new TbFaceModel();
            entity.setUserId(userId);
            entity.setFaceModel(body);
            faceModelDao.insert(entity);
        }


    }

    @Override
    public HashMap searchTodayCheckin(int userId) {
        HashMap todayCheckinMap = checkinDao.searchTodayCheckin(userId);
        return todayCheckinMap;
    }

    @Override
    public long searchCheckinDays(int userId) {
        return checkinDao.searchCheckinDays(userId);
    }

    @Override
    public ArrayList<HashMap> searchWeekCheckin(HashMap checkinMap) {
        ArrayList<HashMap> weekCheckin = checkinDao.searchWeekCheckin(checkinMap);
        ArrayList<String> holidaysInrage = holidaysDao.searchHolidaysInrage(checkinMap);
        ArrayList<String> workDayInRange = workdayDao.searchWorkDayInRange(checkinMap);
        DateTime startDate = DateUtil.parseDate(checkinMap.get("startDate").toString());
        DateTime endDate = DateUtil.parseDate(checkinMap.get("endDate").toString());
        DateRange dateRange = DateUtil.range(startDate, endDate, DateField.DAY_OF_MONTH);
        ArrayList list = new ArrayList();

        dateRange.forEach(dateTime -> {
            String date = dateTime.toString("yyyy-MM-dd");
            //TODO ???????????????????????????????????????
            //????????????????????????????????????
            String type = "?????????";
            if (dateTime.isWeekend()) {
                type = "?????????";
            }
            if (holidaysInrage != null && holidaysInrage.contains(dateTime)) {
                type = "?????????";
            }
            if (workDayInRange != null && workDayInRange.contains(dateTime)) {
                type = "?????????";
            }

            //????????????????????????????????????????????????
            String status = "";
            //???????????????
            if ("?????????".equals(type) && DateUtil.compare(dateTime, DateUtil.date()) <= 0) {
                status = "??????";
                //?????????
                boolean flag = false;
                for (HashMap<String,String> map : weekCheckin) {
                    if (map != null && map.containsValue(date)) {
                        status = map.get("status");
                        flag = true;
                        break;
                    }
                }
                //????????????
                DateTime endTime = DateUtil.parse(DateUtil.today() + " " + sysConstant.attendanceEndTime);
                String today = DateUtil.today();
                if (today.equals(dateTime) && dateTime.isBefore(endTime) && !flag) {
                    status = "";
                }

            }

            //?????????????????????
            HashMap map = new HashMap();
            map.put("date",date);
            map.put("status",status);
            map.put("type",type);
            map.put("day", dateTime.dayOfWeekEnum().toChinese("???"));
            list.add(map);
        });

        return list;

    }

    @Override
    public ArrayList<HashMap> searchMonthCheckin(HashMap checkinMap) {
        return searchWeekCheckin(checkinMap);
    }


}
