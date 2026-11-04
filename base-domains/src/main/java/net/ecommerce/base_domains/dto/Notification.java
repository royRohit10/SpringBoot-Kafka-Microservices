package net.ecommerce.base_domains.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Notification {

    private String recipient;
    private String subject;
    private String message;
    private String createdAt;
}
