package com.example.emoswx.config;

import lombok.Data;
import org.springframework.stereotype.Component;

/**
 * @ClassName SysConstant
 * @Date 2022/1/23 16:00
 * @Author Admin
 * @Description
 */
@Data
@Component
public class SysConstant {
    public String attendanceStartTime;
    public String attendanceTime;
    public String attendanceEndTime;
    public String closingStartTime;
    public String closingTime;
    public String closingEndTime;
}
