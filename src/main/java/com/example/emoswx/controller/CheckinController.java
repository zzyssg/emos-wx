package com.example.emoswx.controller;

import cn.hutool.core.date.DateTime;
import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.hash.Hash;
import com.example.emoswx.common.util.R;
import com.example.emoswx.config.SysConstant;
import com.example.emoswx.config.shiro.JwtUtil;
import com.example.emoswx.controller.form.CheckinForm;
import com.example.emoswx.controller.form.SearchMonthCheckinForm;
import com.example.emoswx.exception.EmosException;
import com.example.emoswx.service.CheckinService;
import com.example.emoswx.service.UserService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

/**
 * @ClassName CheckinController
 * @Date 2022/1/23 18:00
 * @Author Admin
 * @Description
 */
@RestController
@RequestMapping("/checkin")
@Slf4j
@Api("签到模块接口")
public class CheckinController {

    @Value("${emos.image-folder}")
    String imageFolder;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private CheckinService checkinService;

    @Autowired
    private UserService userService;

    @Autowired
    private SysConstant sysConstant;

    @GetMapping("/validCanCheckIn")
    public R validCanCheckin(@RequestHeader("token") String token) {
        int userId = jwtUtil.getUserId(token);
        String res = checkinService.validCanCheckin(userId, DateUtil.today());
        return R.ok(res);
    }

    @PostMapping("/checkin")
    @ApiOperation("签到")
    public R checkin(@Valid CheckinForm checkinForm, @RequestParam("photo") MultipartFile file,
                     @RequestHeader("token") String token) {
        //没有上传文件
        if (null == file) {
            return R.error("没有上传文件");
        }
        //保存文件
        int userId = jwtUtil.getUserId(token);
        String fileName = file.getOriginalFilename().toLowerCase();
        String path = imageFolder + "/" + fileName;
        if (!fileName.endsWith(".jpg")) {
            FileUtil.del(path);
            return R.error("必须提交jpg格式的图片");
        } else {
            try {
                file.transferTo(Paths.get(path));
                HashMap param = new HashMap();
                param.put("userId",userId);
                param.put("path",path);
                param.put("city", checkinForm.getCity());
                param.put("district",checkinForm.getDistrict());
                param.put("address",checkinForm.getAddress());
                param.put("country",checkinForm.getCountry());
                param.put("province",checkinForm.getProvince());
                checkinService.checkin(param);
                return R.ok("签到成功");
            } catch (IOException e) {
                log.error(e.getMessage());
                throw new EmosException("保存图片错误");
            }finally {
                FileUtil.del(path);
            }
        }
    }

    @PostMapping("/createFaceModel")
    @ApiOperation("创建人脸模型")
    public R createFaceModel(@RequestParam("photo") MultipartFile file,
                             @RequestHeader("token") String token) {
        //保存文件
        int userId = jwtUtil.getUserId(token);
        String fileName = file.getOriginalFilename().toLowerCase();
        String path = imageFolder + "/" + fileName;
        if (!fileName.endsWith(".jpg")) {
            FileUtil.del(path);
            return R.error("必须提交jpg格式的图片");
        } else {
            try {
                file.transferTo(Paths.get(path));
                log.info(path);
                checkinService.createFaceModel(userId,path);
                return R.ok("人脸建模成功！");
            } catch (IOException e) {
                log.error(e.getMessage());
                throw new EmosException("保存图片错误");
            }finally {
                FileUtil.del(path);
            }
        }

    }

    @GetMapping("/searchTodayCheckin")
    @ApiOperation("查询当日签到数据")
    public R searchTodayCheckin(@RequestHeader("token") String token) {
        int userId = jwtUtil.getUserId(token);
        HashMap todayCheckinMap = checkinService.searchTodayCheckin(userId);
        todayCheckinMap.put("attendanceTime", sysConstant.attendanceTime);
        todayCheckinMap.put("closingTime", sysConstant.closingTime);
        long checkinDaysCount = checkinService.searchCheckinDays(userId);
        todayCheckinMap.put("checkinDays", checkinDaysCount);

        //判断日期是否在入职日期之前
        DateTime beginOfWeek = DateUtil.beginOfWeek(DateUtil.date());
        DateTime hiredate = DateUtil.parse(userService.searchHiredate(userId));
        if(DateUtil.compare(beginOfWeek,hiredate) < 0){
            beginOfWeek = hiredate;
        }
        DateTime endOfWeek = DateUtil.endOfWeek(DateUtil.date());
        HashMap checkinMap = new HashMap();
        checkinMap.put("startDate", beginOfWeek.toString());
        checkinMap.put("endDate", endOfWeek.toString());
        checkinMap.put("userId", userId);
        ArrayList<HashMap> weekCheckin = checkinService.searchWeekCheckin(checkinMap);
        todayCheckinMap.put("weekCheckin", weekCheckin);
        return R.ok().put("result", todayCheckinMap);
    }

    @PostMapping("/searchMonthCheckin")
    @ApiOperation("查询月度考勤")
    public R searchMonthCheckin(@RequestBody SearchMonthCheckinForm searchMonthCheckinForm, @RequestHeader("token") String token) {
        int userId = jwtUtil.getUserId(token);
        log.info(userService.searchHiredate(userId));
        DateTime hiredate = DateUtil.parse(userService.searchHiredate(userId));
        DateTime startDate = DateUtil.parse(searchMonthCheckinForm.getYear() + "-" + searchMonthCheckinForm.getMonth() + "-01");
        if(startDate.isBefore(DateUtil.beginOfMonth(hiredate))){
            throw new EmosException("只能查询入职日期之后的考勤");
        }
        if (startDate.isBefore(hiredate)) {
            startDate = hiredate;
        }
        DateTime endDate = DateUtil.endOfMonth(startDate);
        HashMap checkinMap = new HashMap();
        checkinMap.put("startDate",startDate.toString("yyyy-MM-dd"));
        checkinMap.put("endDate",endDate.toString("yyyy-MM-dd"));
        checkinMap.put("userId",userId);

        ArrayList<HashMap> monthCheckinList = checkinService.searchMonthCheckin(checkinMap);
        //遍历月度考勤
        int sumNormal = 0, sumLate = 0, sumAbsent = 0;
        for (HashMap map : monthCheckinList) {
            String type = (String) map.get("type");
            String status = (String) map.get("status");
            if("工作日".equals(type)){
                if("正常".equals(status)){
                    sumNormal++;
                } else if ("迟到".equals(status)) {
                    sumLate++;
                } else if ("缺勤".equals(status)) {
                    sumAbsent++;
                }
            }
        }
        return R.ok().put("sumNormal", sumNormal)
                .put("sumLate", sumLate)
                .put("sumAbsent", sumAbsent)
                .put("list",monthCheckinList);

    }

}
