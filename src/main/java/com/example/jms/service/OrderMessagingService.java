package com.example.jms.service;

import com.example.jms.model.Order;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.jms.core.JmsClient;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.Optional;

@Service
public class OrderMessagingService {

    private static final Logger log = LoggerFactory.getLogger(OrderMessagingService.class);
    private final JmsClient jmsClient;

    public OrderMessagingService(JmsClient jmsClient) {
        this.jmsClient = jmsClient;
    }

    public void sendSimpleOrder(Order order) {
        log.info("Sending order with basic fluent API: {}", order.orderId());

        jmsClient
                .destination("order-queue")
                .send(order);
    }

    public void sendPriorityOrder(Order order) {
        log.info("Sending priority order with QoS settings: {}", order.orderId());

        jmsClient
                .destination("order-queue")
                .withTimeToLive(300000)
                .withPriority(9)
                .withDeliveryDelay(1000)
                .send(order);
    }

    public void sendOrderWithMetadata(Order order, Map<String, Object> metadata) {
        log.info("Sending order with metadata headers: {}", order.orderId());

        Message<Order> message = MessageBuilder
                .withPayload(order)
                .setHeader("source", "web-portal")
                .setHeader("region", metadata.get("region"))
                .setHeader("processedBy", "jms-client-demo")
                .build();

        jmsClient
                .destination("order-queue")
                .withTimeToLive(60000)
                .send(message);
    }

    public Optional<Order> receiveOrder() {
        log.info("Receiving order with timeout...");

        Optional<Message<?>> message = jmsClient
                .destination("order-queue")
                .withReceiveTimeout(5000)
                .receive();

        return message.map(msg -> {
            Order order = (Order) msg.getPayload();
            log.info("Received order: {}", order.orderId());
            return order;
        });
    }

    public Optional<Order> receiveAndConvertOrder() {
        log.info("Receiving and converting order...");

        return jmsClient
                .destination("order-queue")
                .withReceiveTimeout(3000)
                .receive(Order.class);
    }

    public Order processOrderSynchronously(Order order) {
        log.info("Processing order synchronously: {}", order.orderId());

        Message<Order> request = MessageBuilder
                .withPayload(order)
                .setHeader("operation", "process")
                .build();

        Optional<Message<?>> reply = jmsClient
                .destination("order-processor")
                .withReceiveTimeout(10000)
                .sendAndReceive(request);

        return reply
                .map(msg -> (Order) msg.getPayload())
                .orElseThrow(() -> new RuntimeException("No reply received"));
    }

    public void demonstrateReusableHandle() {
        // Create a reusable handle with preset QoS
        var expressHandle = jmsClient
                .destination("express-orders")
                .withTimeToLive(60000)
                .withPriority(8);

        Order order1 = new Order("EXP-001", "CUST-1", new BigDecimal("99.99"),
                Order.OrderStatus.PENDING, LocalDateTime.now());
        Order order2 = new Order("EXP-002", "CUST-2", new BigDecimal("149.99"),
                Order.OrderStatus.PENDING, LocalDateTime.now());

        expressHandle.send(order1);
        expressHandle.send(order2);

        log.info("Sent multiple express orders using reusable handle");
    }
}