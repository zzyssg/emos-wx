package com.example.emoswx.db.dao;

import cn.hutool.core.date.DateField;
import cn.hutool.core.date.DateUtil;
import cn.hutool.json.JSONObject;
import com.example.emoswx.db.pojo.MessageEntity;
import com.example.emoswx.db.pojo.MessageRefEntity;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.stereotype.Repository;
import springfox.documentation.spring.web.json.Json;

import java.util.Date;
import java.util.HashMap;
import java.util.List;

/**
 * @ClassName MessageDao
 * @Date 2022/2/10 15:51
 * @Author Admin
 * @Description
 */
@Repository
@Slf4j
public class MessageDao {


    /*向mongoDB插入记录的模板*/
    @Autowired
    public MongoTemplate mongoTemplate;

    /*返回插入的记录的主键*/
    public String insert(MessageEntity messageEntity) {
        Date sendTime = new Date();
        sendTime = DateUtil.offset(sendTime, DateField.HOUR, 8);
        messageEntity.setSendTime(sendTime);

        MessageEntity entity = mongoTemplate.insert(messageEntity,"message");
        return entity.get_id();
    }

    /*分页查询*/
    public List<HashMap> searchMessageByPage1(int userId, long start, int length) {
        JSONObject jsonObject = new JSONObject();
        //TODO 什么意思
        jsonObject.set("$toString", "$_id");
        Aggregation aggregation = Aggregation.newAggregation(
                Aggregation.addFields().addField("id").withValue(jsonObject).build(),
                Aggregation.lookup("message_ref", "id", "messageId", "ref"),
                Aggregation.match(Criteria.where("ref.receiverId").is(userId)),
                Aggregation.sort(Sort.by(Sort.Direction.DESC, "sendTime")),
                Aggregation.skip(start),
                Aggregation.limit(length)
        );
        AggregationResults<HashMap> results = mongoTemplate.aggregate(aggregation, "message", HashMap.class);
        log.info("results:", results);
        //返回的结果
        List<HashMap> list = results.getMappedResults();
        list.forEach(one -> {
            log.info("one: " +one);
            List<MessageRefEntity> refList = (List<MessageRefEntity>) one.get("ref");
            MessageRefEntity refEntity = refList.get(0);
            boolean readFlag = refEntity.getReadFlag();
            String refId = refEntity.get_id();
            one.remove("ref");
            one.put("readFlag", readFlag);
            one.put("refId", refId);
            one.remove("_id");

            /*北京时间比格林尼治时间晚8小时*/
            /*格林尼治时间转为 北京时间*/
            Date sendTime = (Date) one.get("sendTime");
            sendTime = DateUtil.offset(sendTime, DateField.HOUR, -8);

            /*如果是今天的消息，仅仅显示时间；若是之前的消息，则仅仅显示日期*/
            String today = DateUtil.today();
            String sendTimeDate = DateUtil.format(sendTime, "yyyy-MM-dd");
            /*如果不是今天的，仅仅显示日期*/
            if (!today.equals(sendTimeDate)) {
                one.put("sendTime", sendTimeDate);
            } else {
                one.put("sendTime", DateUtil.format(sendTime, "HH:mm"));
            }
        });
        return list;
    }

    public List<HashMap> searchMessageByPage(int userId,long start,int length){
        JSONObject json=new JSONObject();
        json.set("$toString","$_id");
        Aggregation aggregation=Aggregation.newAggregation(
                Aggregation.addFields().addField("id").withValue(json).build(),
                Aggregation.lookup("message_ref","id","messageId","ref"),
                Aggregation.match(Criteria.where("ref.receiverId").is(userId)),
                Aggregation.sort(Sort.by(Sort.Direction.DESC,"sendTime")),
                Aggregation.skip(start),
                Aggregation.limit(length)
        );
        AggregationResults<HashMap> results=mongoTemplate.aggregate(aggregation,"message",HashMap.class);
        List<HashMap> list=results.getMappedResults();
        list.forEach(one->{
            List<MessageRefEntity> refList= (List<MessageRefEntity>) one.get("ref");
            MessageRefEntity entity=refList.get(0);
            boolean readFlag=entity.getReadFlag();
            String refId=entity.get_id();
            one.put("readFlag",readFlag);
            one.put("refId",refId);
            one.remove("ref");
            one.remove("_id");
            Date sendTime= (Date) one.get("sendTime");
            sendTime=DateUtil.offset(sendTime,DateField.HOUR,-8);

            String today=DateUtil.today();
            if(today.equals(DateUtil.date(sendTime).toDateStr())){
                one.put("sendTime",DateUtil.format(sendTime,"HH:mm"));
            }
            else{
                one.put("sendTime",DateUtil.format(sendTime,"yyyy/MM/dd"));
            }
        });
        return list;
    }

    /*根据消息id查询消息详情*/
    public HashMap searchMessgaeById(String messageId) {
        HashMap message = mongoTemplate.findById(messageId, HashMap.class, "message");
        Date sendTime = (Date) message.get("sendTime");
        /*将日期转为北京时间*/
        sendTime = DateUtil.offset(sendTime, DateField.HOUR, -8);
        message.replace("sendTime", DateUtil.format(sendTime, "yyyy-MM-dd HH:mm"));
        return message;
    }

}
