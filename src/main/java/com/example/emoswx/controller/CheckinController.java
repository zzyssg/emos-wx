package com.example.emoswx.controller;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.lang.hash.Hash;
import com.example.emoswx.common.util.R;
import com.example.emoswx.config.shiro.JwtUtil;
import com.example.emoswx.controller.form.CheckinForm;
import com.example.emoswx.exception.EmosException;
import com.example.emoswx.service.CheckinService;
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

    @Value("emos.image_folder")
    String imageFolder;

    @Autowired
    private JwtUtil jwtUtil;

    private CheckinService checkinService;

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
        if (null == null) {
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

}
