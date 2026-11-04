package net.ecommerce.base_domains.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.Map;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AnalyticsResponse implements Serializable {

    private Long totalOrders;
    private Long confirmedOrders;
    private Long cancelledOrders;
    private double totalRevenue;
    private Map<Long,Double> productSales;
}
