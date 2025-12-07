package com.example.jms.config;

import jakarta.jms.Queue;
import org.apache.activemq.artemis.jms.client.ActiveMQQueue;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ActiveMqConfig {

    @Bean
    public Queue orderQueue() {
        return new ActiveMQQueue("order-queue");
    }

    @Bean
    public Queue notificationQueue() {
        return new ActiveMQQueue("notification-queue");
    }
}
