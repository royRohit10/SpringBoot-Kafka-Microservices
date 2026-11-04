package net.ecommerce.stock_service.entities;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Entity
@Table(name = "product_analytics")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ProductAnalyticsEntity implements Serializable {

    @Id
    private Long productId;

    private Long totalQuantitySold;
    private Long totalOrders;
    private Long confirmedOrders;
    private Long cancelledOrders;
    private Double revenueGenerated;
}
