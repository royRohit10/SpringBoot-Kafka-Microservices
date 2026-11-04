package net.ecommerce.email_service;

import net.ecommerce.base_domains.dto.OrderRequestedEvent;
import net.ecommerce.base_domains.dto.OrderStatus;
import net.ecommerce.base_domains.dto.OrderUpdatedEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class KafkaConsumerService {

    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaConsumerService.class);

    @Autowired
    private MailSenderService mailSenderService;


    @KafkaListener(topics = "${spring.kafka.topic.name}",
            groupId = "${spring.kafka.consumer.group-id}"
    )
    public void consumeMessage(OrderUpdatedEvent event){
        LOGGER.info(String.format("Event received by Email service -> %s",event.toString()));

        if(event.getStatus() == OrderStatus.CONFIRMED) {
            String subject = "Order Placed!!! Thank You for buying from Here";
            String body = event.getOrder().toString();
            mailSenderService.sendMessage(event.getOrder().getCustomerEmail(),
                    subject, body);
        }
        else
            LOGGER.info(String.format("No mail has been send"));
    }
}
