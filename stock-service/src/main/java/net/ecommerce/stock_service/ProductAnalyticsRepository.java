package net.ecommerce.stock_service;

import net.ecommerce.stock_service.entities.ProductAnalyticsEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductAnalyticsRepository extends JpaRepository<ProductAnalyticsEntity,Long> {

    List<ProductAnalyticsEntity> findAllByOrderByRevenueGeneratedDesc();
}
