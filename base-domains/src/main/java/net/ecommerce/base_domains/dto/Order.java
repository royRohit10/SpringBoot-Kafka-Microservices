package net.ecommerce.base_domains.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Order {

    private Long orderId;
    private Long productId;
    private Integer qty;
    private Double totalAmount;
    private String customerName;
    private String customerEmail;

}


