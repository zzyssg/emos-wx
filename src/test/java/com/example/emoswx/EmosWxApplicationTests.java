package com.example.emoswx;

import cn.hutool.core.date.DateUtil;
import cn.hutool.core.util.IdUtil;
import com.example.emoswx.db.pojo.MessageEntity;
import com.example.emoswx.db.pojo.MessageRefEntity;
import com.example.emoswx.db.pojo.TbMeeting;
import com.example.emoswx.service.MeetingService;
import com.example.emoswx.service.MessageService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Date;

@SpringBootTest
@Slf4j
class EmosWxApiApplicationTests {
    @Autowired
    private MessageService messageService;

    @Autowired
    private MeetingService meetingService;

    @Test
    void contextLoads() {
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

    @Test
    void createMeetingsTest() {
        for (int i = 0; i < 100; i++) {
            TbMeeting meeting = new TbMeeting();
            Date date = new Date();
            meeting.setCreateTime(date);
            log.info("会议创建时间：" ,date);
            meeting.setCreatorId(8L);
            meeting.setDate(DateUtil.today());
            meeting.setDesc("test");
            meeting.setEnd("10:00");
            meeting.setMembers("[8,10,11]");
            meeting.setUuid(IdUtil.simpleUUID());
            meeting.setStart("09:00");
            meeting.setInstanceId(IdUtil.simpleUUID());
            meeting.setPlace("线上会议");
            meeting.setTitle("第" + i + "个会议");
            meeting.setType((short) 1);
            meeting.setStatus((short) 3);
            meetingService.insertMeeting(meeting);

        }
    }
}

