package com.example.emoswx.db.dao;

import com.example.emoswx.db.pojo.MessageRefEntity;
import com.mongodb.client.result.DeleteResult;
import com.mongodb.client.result.UpdateResult;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Repository;

/**
 * @ClassName MessageRefDao
 * @Date 2022/2/10 16:32
 * @Author Admin
 * @Description
 */
@Repository
@Slf4j
public class MessageRefDao {

    @Autowired
    private MongoTemplate mongoTemplate;

    public String insert(MessageRefEntity messageRefEntity) {
            MessageRefEntity res = mongoTemplate.insert(messageRefEntity,"message_ref");
            return res.get_id();

    }

    /*查询未读消息数量*/
    public long searchUnreadCount(int userId) {
        //返回更新最新消息的数量-即为未读消息的数量
        Query query = new Query();
        query.addCriteria(Criteria.where("readFlag").is(false).and("receiver").is(userId));
        Update update = new Update();
        update.set("lastFlag", false);
        UpdateResult result = mongoTemplate.updateMulti(query, update, "message_ref");
        long rows = result.getModifiedCount();
        return rows;
    }

    /*查询新接收消息*/
    public long searchLastCount(int userId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("userId").is(userId).and("lastFlag").is(true));
        Update update = new Update();
        update.set("lastFlag", false);
        UpdateResult result = mongoTemplate.updateMulti(query, update, "message_ref");
        long rows = result.getModifiedCount();
        return rows;
    }


    /*把未读消息更改为已读*/
    public long updateUnreadMessage(String messageId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(messageId));
        Update update = new Update();
        update.set("readFlag", true);
        UpdateResult result = mongoTemplate.updateFirst(query,update,"message_ref");
        long rows = result.getModifiedCount();
        return rows;
    }

    /*根据id删除ref消息*/
    public long deleteMessageRefByid(String messageId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("_id").is(messageId));
        DeleteResult result = mongoTemplate.remove(query, "message_ref");
        long rows = result.getDeletedCount();
        return rows;
    }

    /*删除某个用户的全部信息*/
    public long deleteUserMessageRef(int userId) {
        Query query = new Query();
        query.addCriteria(Criteria.where("receiverId").is(userId));
        DeleteResult result = mongoTemplate.remove(query, "message_ref");
        long rows = result.getDeletedCount();
        return rows;
    }


}
