package net.ecommerce.stock_service.entities;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

@Data
@Entity
@Table(name = "analytics_summary")
@AllArgsConstructor
@NoArgsConstructor
public class AnalyticsSummaryEntity implements Serializable {

    @Id
    private Long id;

    private Long totalOrders;
    private Long confirmedOrders;
    private Long cancelledOrders;
    private Double totalRevenue;
}
