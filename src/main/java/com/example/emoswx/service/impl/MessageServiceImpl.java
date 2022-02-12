package com.example.emoswx.service.impl;

import com.example.emoswx.db.dao.MessageDao;
import com.example.emoswx.db.dao.MessageRefDao;
import com.example.emoswx.db.pojo.MessageEntity;
import com.example.emoswx.db.pojo.MessageRefEntity;
import com.example.emoswx.service.MessageService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;

/**
 * @ClassName MessageServiceImpl
 * @Date 2022/2/10 17:04
 * @Author Admin
 * @Description
 */
@Service
public class MessageServiceImpl implements MessageService {


    @Autowired
    private MessageDao messageDao;

    @Autowired
    private MessageRefDao messageRefDao;

    @Override
    public String insertMessage(MessageEntity messageEntity) {
        String messageId =  messageDao.insert(messageEntity);
        return messageId;
    }

    @Override
    public String insertRef(MessageRefEntity messageRefEntity) {
        String messageRefId = messageRefDao.insert(messageRefEntity);
        return messageRefId;
    }

    @Override
    public long searchUnreadCount(int userId) {
        long unreadCount = messageRefDao.searchUnreadCount(userId);
        return unreadCount;
    }

    @Override
    public long searchLastCount(int userId) {
        long lastCount = messageRefDao.searchLastCount(userId);
        return lastCount;

    }

    @Override
    public List<HashMap> searchMessageByPage(int userId, long start, int length) {
        List<HashMap> messageByPageList = messageDao.searchMessageByPage(userId, start, length);
        return messageByPageList;
    }

    @Override
    public HashMap searchMessageById(String id) {
        HashMap messgaeById = messageDao.searchMessgaeById(id);
        return messgaeById;
    }

    @Override
    public long updateUnreadMessage(String id) {
        long rows = messageRefDao.updateUnreadMessage(id);
        return rows;
    }

    @Override
    public long deleteMessageRefById(String id) {
        long rows = messageRefDao.deleteMessageRefByid(id);
        return rows;
    }

    @Override
    public long deleteUserMessageRef(int userId) {
        long rows = messageRefDao.deleteUserMessageRef(userId);
        return rows;
    }
}
