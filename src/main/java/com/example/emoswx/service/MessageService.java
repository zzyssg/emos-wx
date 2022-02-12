package com.example.emoswx.service;

import com.example.emoswx.db.pojo.MessageEntity;
import com.example.emoswx.db.pojo.MessageRefEntity;

import java.util.HashMap;
import java.util.List;

/**
 * @ClassName MessageService
 * @Date 2022/2/10 16:58
 * @Author Admin
 * @Description
 */
public interface MessageService {
    public String insertMessage(MessageEntity messageEntity) ;

    public String insertRef(MessageRefEntity messageRefEntity);

    public long searchUnreadCount(int userId);

    public long searchLastCount(int userId);

    public List<HashMap> searchMessageByPage(int userId, long start, int length);

    public HashMap searchMessageById(String id);

    public long updateUnreadMessage(String id);


    public long deleteMessageRefById(String id);

    public long deleteUserMessageRef(int userId);

}
