package net.ecommerce.stock_service;

import net.ecommerce.stock_service.entities.AnalyticsSummaryEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AnalyticsSummaryRepository extends JpaRepository<AnalyticsSummaryEntity,Long> {
}
