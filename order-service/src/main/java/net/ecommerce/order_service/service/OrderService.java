package net.ecommerce.order_service.service;

import net.ecommerce.base_domains.dto.*;
import net.ecommerce.order_service.InventoryFeignClient;
import net.ecommerce.order_service.entity.OrderEntity;
import net.ecommerce.order_service.repository.OrderRepository;
import org.apache.kafka.clients.admin.NewTopic;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Service
public class OrderService {

    @Autowired
    private KafkaTemplate<String,OrderRequestedEvent> kafkaTemplate;
    @Autowired
    private InventoryFeignClient feignClient;

    @Autowired
    private static final Logger LOGGER = LoggerFactory.getLogger(OrderService.class);

    @Autowired
    private NewTopic topic;
    @Autowired
    private OrderRepository orderRepository;


    public void placeOrder(Order order) {

        OrderRequestedEvent event = new OrderRequestedEvent();
        if(order.getQty() <= 0 || getProductsById(order.getProductId()) == null)
            throw new RuntimeException("Invalid Selection");


        event.setStatus(OrderStatus.REQUESTED);
        event.setMessage("Order in Requested Stage");
        event.setOrder(order);
        event.setCreatedAt(LocalDateTime.now());

        Double price = order.getQty() * getProductsById(order.getProductId()).getPrice();
        order.setTotalAmount(price);

        event.setOrder(order);

        Message<OrderRequestedEvent> message = MessageBuilder.withPayload(event)
                .setHeader(KafkaHeaders.TOPIC,topic.name())
                .build();

        LOGGER.info(String.format("Order Requested Event has been published"));
        kafkaTemplate.send(message);

    }

    @KafkaListener(topics = "order_update",groupId = "${spring.kafka.consumer.group-id}")
    public void manageUpdatedOrder(OrderUpdatedEvent event){

        Order order = event.getOrder();

        if(event.getStatus() == OrderStatus.CANCELLED){
            LOGGER.info("Cancelled order has not been stored");
            return;
        }
        OrderEntity entity = new OrderEntity(
                order.getOrderId(),
                order.getProductId(),
                order.getQty(),
                order.getTotalAmount(),
                order.getCustomerName(),
                order.getCustomerEmail()
        );

        orderRepository.save(entity);
        LOGGER.info("Order is confirmed");
    }

    public Product getProductsById(Long id){
        return feignClient.getProductById(id);
    }

    public List<Product> getAllProducts(){
        return feignClient.getAllProducts();
    }
}
