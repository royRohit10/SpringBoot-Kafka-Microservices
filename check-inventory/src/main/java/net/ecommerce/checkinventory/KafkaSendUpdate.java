package net.ecommerce.checkinventory;

import net.ecommerce.base_domains.dto.OrderRequestedEvent;
import net.ecommerce.base_domains.dto.OrderUpdatedEvent;
import org.apache.kafka.clients.admin.NewTopic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Service;

@Service
public class KafkaSendUpdate {

    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaSendUpdate.class);

    @Autowired
    private NewTopic topic;


    @Autowired
    private KafkaTemplate<String, OrderUpdatedEvent> kafkaTemplate;


    public void sendMessage(OrderUpdatedEvent orderUpdatedEvent){

        LOGGER.info(String.format("Final Update send->%s",orderUpdatedEvent.toString()));

        Message<OrderUpdatedEvent> message = MessageBuilder.withPayload(orderUpdatedEvent)
                .setHeader(KafkaHeaders.TOPIC,topic.name())
                .build();

        kafkaTemplate.send(message);
    }
}
