package net.ecommerce.base_domains.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class OrderRequestedEvent {

    private Long orderId;
    private Order order;
    private OrderStatus status;
    private String message;
    private LocalDateTime createdAt;
}
