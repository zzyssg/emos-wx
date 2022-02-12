package com.example.emoswx.controller;

import cn.hutool.core.util.IdUtil;
import com.example.emoswx.common.util.R;
import com.example.emoswx.db.pojo.MessageEntity;
import com.example.emoswx.db.pojo.MessageRefEntity;
import com.example.emoswx.service.MessageService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Date;

/**
 * @author ZZy
 * @date 2022/1/8 13:45
 * @description
 */


@RestController
@RequestMapping("/test")
@Api("测试第一个web接口")
public class TestController {

    @Autowired
    MessageService messageService;

    @GetMapping("/sayHello")
    @ApiOperation("测试第一个方法")
    public void sayHello() {
        for (int i = 1; i <= 100; i++) {
            MessageEntity message = new MessageEntity();
            message.setUuid(IdUtil.simpleUUID());
            message.setSenderId(0);
            message.setSenderName("系统消息");
            message.setMsg("这是第" + i + "条测试消息");
            message.setSendTime(new Date());
            String id=messageService.insertMessage(message);

            MessageRefEntity ref=new MessageRefEntity();
            ref.setMessageId(id);
            ref.setReceiverId(8); //注意：这是接收人ID
            ref.setLastFlag(true);
            ref.setReadFlag(false);
            messageService.insertRef(ref);
        }
    }

}
