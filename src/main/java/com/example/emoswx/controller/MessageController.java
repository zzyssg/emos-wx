package com.example.emoswx.controller;

import com.example.emoswx.common.util.R;
import com.example.emoswx.config.shiro.JwtUtil;
import com.example.emoswx.controller.form.DeleteMessageRefByIdForm;
import com.example.emoswx.controller.form.SearchMessageByIdForm;
import com.example.emoswx.controller.form.SearchMessageByPageForm;
import com.example.emoswx.controller.form.UpdateUnreadMessageForm;
import com.example.emoswx.service.MessageService;
import com.example.emoswx.task.MessageTask;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.HashMap;
import java.util.List;

/**
 * @ClassName MessageController
 * @Date 2022/2/10 17:30
 * @Author Admin
 * @Description
 */
@RestController
@RequestMapping("/message")
@Api("网络消息接口")
public class MessageController {

    @Autowired
    JwtUtil jwtUtil;

    @Autowired
    MessageService messageService;

    @Autowired
    MessageTask messageTask;

    @PostMapping("/searchMessageByPage")
    @ApiOperation("分页查询")
    public R searchMessageByPage(@Valid @RequestBody SearchMessageByPageForm searchMessageByPageForm,
                               @RequestHeader("token") String token) {
        int userId = jwtUtil.getUserId(token);
        int page = searchMessageByPageForm.getPage();
        int length = searchMessageByPageForm.getLength();
        long start = (page - 1) * length;
        List<HashMap> messageByPage = messageService.searchMessageByPage(userId, start, length);
        return R.ok().put("result", messageByPage);
    }

    @PostMapping("/searchMessageById")
    public R searchMessageById(@Valid @RequestBody SearchMessageByIdForm messageForm) {
        String messageId = messageForm.getId();
        HashMap messageById = messageService.searchMessageById(messageId);
        return R.ok().put("result", messageById);
    }

    @PostMapping("/updateUnreadMessage")
    public R updateUnreadMessage(@Valid @RequestBody UpdateUnreadMessageForm messageForm) {
        long rows = messageService.updateUnreadMessage(messageForm.getId());
        return R.ok().put("result", rows == 1 ? true : false);
    }

    @PostMapping("/deleteMessageRefById")
    public R deleteMessageRefById(@Valid @RequestBody DeleteMessageRefByIdForm messageRefByIdForm) {
        String messageId = messageRefByIdForm.getId();
        long rows = messageService.deleteMessageRefById(messageId);
        return R.ok().put("result", rows == 1 ? true : false);

    }

    @GetMapping("/refreshMessage")
    @ApiOperation("刷新用户消息")
    public R refreshMessage(@RequestHeader("token") String token) {
        int userId = jwtUtil.getUserId(token);
        //异步接收消息
        messageTask.receiveAsync(userId + "");
        //查询接收了多少消息
        long lastCount = messageService.searchLastCount(userId);
        //查询未读数据
        long unreadCount = messageService.searchUnreadCount(userId);
        return R.ok().put("lastRows", lastCount).put("unreadRows", unreadCount);

    }

}
