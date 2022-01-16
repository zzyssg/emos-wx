package com.example.emoswx;

import io.swagger.annotations.ApiOperation;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.web.servlet.ServletComponentScan;

@SpringBootApplication
@ServletComponentScan
public class EmosWxApplication {

    public static void main(String[] args) {
        SpringApplication.run(EmosWxApplication.class, args);
    }

}
