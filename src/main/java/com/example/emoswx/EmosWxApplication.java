package com.example.emoswx;

import cn.hutool.core.util.StrUtil;
import com.example.emoswx.config.SysConstant;
import com.example.emoswx.db.dao.SysConfigDao;
import com.example.emoswx.db.pojo.SysConfig;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

import javax.annotation.PostConstruct;
import java.lang.reflect.Field;
import java.util.List;

@SpringBootApplication
@ServletComponentScan
@Slf4j
public class EmosWxApplication {

    @Autowired
    SysConfigDao sysConfigDao;

    @Autowired
    SysConstant sysConstant;

    public static void main(String[] args) {
        SpringApplication.run(EmosWxApplication.class, args);
    }

    @PostConstruct
    public void init(){
        //获取SysConfig,设置key、value
        List<SysConfig> sysConfigs = sysConfigDao.selectAllParams();
        for (SysConfig sysConfig : sysConfigs) {
            String key = StrUtil.toCamelCase(sysConfig.getParamKey());
            String value = sysConfig.getParamValue();
            try {
                Field field = sysConstant.getClass().getDeclaredField(key);
                field.set(sysConstant,value);
            } catch (Exception e) {
                log.error("执行异常",e);
            }

        }

    }

}
