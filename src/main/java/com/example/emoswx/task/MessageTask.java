package com.example.emoswx.task;

import ch.qos.logback.core.encoder.EchoEncoder;
import com.example.emoswx.db.pojo.MessageEntity;
import com.example.emoswx.db.pojo.MessageRefEntity;
import com.example.emoswx.exception.EmosException;
import com.example.emoswx.service.MessageService;
import com.rabbitmq.client.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * @ClassName MessageTask
 * @Date 2022/2/10 18:07
 * @Author Admin
 * @Description
 */
@Slf4j
@Component
public class MessageTask {

    @Autowired
    ConnectionFactory factory;

    @Autowired
    MessageService messageService;

    /**
     * 同步发送消息
     *
     * @param topic  主题
     * @param entity 消息对象
     */
    public void send(String topic, MessageEntity entity) {
        //向mongodb插入数据，返回messageId
        String messgeId = messageService.insertMessage(entity);
        //向rabbitMQ发送消息
        try (
                Connection connection = factory.newConnection();
                Channel channel = connection.createChannel();) {
            //链接到某个topic
            channel.queueDeclare(topic, true, false, false, null);
            HashMap header = new HashMap();//存放属性数据
            header.put("messageId", messgeId);
            //创建AMQP协议参数对象，添加附加属性
            AMQP.BasicProperties properties = new AMQP.BasicProperties().builder().headers(header).build();
            channel.basicPublish("", topic, properties, entity.getMsg().getBytes());
            log.info("消息发送成功");
        } catch (Exception e) {
            log.error("消息发送执行异常", e);
            throw new EmosException("向rabbit发送消息执行异常");
        }

    }

    /**
     * 异步发送消息
     *
     * @param topic  主题
     * @param entity 消息对象
     */
    public void sendAsync(String topic, MessageEntity entity) {
        send(topic, entity);
    }

    /**
     * 同步接受数据
     *
     * @param topic
     */
    public int receive(String topic) {
        int i = 0;
        try (
                Connection connection = factory.newConnection();
                Channel channel = connection.createChannel()
        ) {
            //从队列中接收消息，不主动确认
            channel.queueDeclare(topic, true, false, false, null);
            //topic中的数据未知，死循环接收消息
            while (true) {
                //创建响应接收数据，禁止自动发送ack应答
                GetResponse response = channel.basicGet(topic, false);
                if (response != null) {
                    AMQP.BasicProperties properties = response.getProps();
                    Map<String, Object> headers = properties.getHeaders();
                    String messageId = headers.get("messageId").toString();
                    //获取消息正文
                    byte[] body = response.getBody();
                    String message = new String(body);
                    log.debug("message from mq：", message);
                    MessageRefEntity refEntity = new MessageRefEntity();
                    refEntity.setMessageId(messageId);
                    refEntity.setReceiverId(Integer.parseInt(topic));
                    refEntity.setReadFlag(false);
                    refEntity.setLastFlag(true);
                    messageService.insertRef(refEntity);//消息存储在Mongodb中
                    //数据保存到Mongodb后，再发送ack应答，让topic删除此条消息
                    long deliveryTag = response.getEnvelope().getDeliveryTag();
                    channel.basicAck(deliveryTag, false);
                    i++;
                } else {
                    break; //接收不到消息，退出循环
                }
            }
        } catch (Exception e) {
            log.error("接收消息执行异常", e);
        }
        return i;
    }

    /**
     * 异步接收提醒
     *
     * @param  topic
     * */
    public  int receiveAsync(String topic) {
        return receive(topic);
    }

    /**
     * 同步删除消息队列
     *
     * @param topic
     * */
    public void  delteteQueue(String topic) {
        try (
                Connection connection = factory.newConnection();
                Channel channel = connection.createChannel()
        ) {
            channel.queueDelete(topic);
            log.debug("消息队列删除成功");
        } catch (Exception e) {
            log.error("删除消息队列失败", e);
            throw new EmosException("删除队列失败");
        }

    }

    /**
     * 异步删除消息队列
     *
     * @param topic
     */
    public void delteteQueueAsync(String topic) {
        delteteQueue(topic);
    }

}
