package com.example.emoswx.config;

import com.rabbitmq.client.ConnectionFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @ClassName RabbitMQConfig
 * @Date 2022/2/10 18:02
 * @Author Admin
 * @Description
 */
@Configuration
public class RabbitMQConfig {

    @Value("${emos.aliyunLiuyu}")
    private String aliyunLiuyu;

    @Value("${emos.rabbitMQPort}")
    private String rabbitMQPort;

    @Bean
    public ConnectionFactory getFactory() {
        ConnectionFactory factory = new ConnectionFactory();
        factory.setHost("47.103.71.33");
        factory.setPort(Integer.parseInt("5672"));
        return factory;
    }

}
