package net.ecommerce.stock_service.Kafka;

import net.ecommerce.base_domains.dto.*;
import net.ecommerce.stock_service.AnalyticsSummaryRepository;
import net.ecommerce.stock_service.ProductAnalyticsRepository;
import net.ecommerce.stock_service.entities.AnalyticsSummaryEntity;
import net.ecommerce.stock_service.entities.ProductAnalyticsEntity;
import org.jspecify.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Service
public class KafkaStockService {

    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaStockService.class);

    @Autowired
    private AnalyticsSummaryRepository analyticsSummaryRepository;
    @Autowired
    private ProductAnalyticsRepository productAnalyticsRepository;
    @Autowired
    private RedisTemplate<String,Object> redisTemplate;


    @KafkaListener(topics = "${spring.kafka.topic.name}",
            groupId = "${spring.kafka.consumer.group-id}")
    public void consume(OrderUpdatedEvent event){

        LOGGER.info(String.format("Event Received in Stock Service -> %s",event.toString()));


        Order order = event.getOrder();
        Long productId = order.getProductId();

        Optional<ProductAnalyticsEntity> boxSummaryEntity = productAnalyticsRepository.findById(productId);

        ProductAnalyticsEntity productAnalytics;
        if(boxSummaryEntity.isPresent()) {
            productAnalytics = boxSummaryEntity.get();
            productAnalytics.setTotalOrders(productAnalytics.getTotalOrders() + 1);
            if (event.getStatus() == OrderStatus.CONFIRMED) {
                productAnalytics.setRevenueGenerated(productAnalytics.getRevenueGenerated() + order.getTotalAmount());
                productAnalytics.setConfirmedOrders(productAnalytics.getConfirmedOrders() + 1);
                productAnalytics.setTotalQuantitySold(productAnalytics.getTotalQuantitySold() + order.getQty());
            }
            else {
                productAnalytics.setCancelledOrders(productAnalytics.getCancelledOrders() + 1);
            }
        }
        else {
            productAnalytics = new ProductAnalyticsEntity();
            productAnalytics.setProductId(productId);
            productAnalytics.setTotalOrders(1L);
            productAnalytics.setConfirmedOrders(0L);
            productAnalytics.setCancelledOrders(0L);
            productAnalytics.setRevenueGenerated(0.0);
            productAnalytics.setTotalQuantitySold(0L);
            if (event.getStatus() == OrderStatus.CONFIRMED) {
                productAnalytics.setRevenueGenerated(order.getTotalAmount());
                productAnalytics.setConfirmedOrders(1L);
                productAnalytics.setTotalQuantitySold(Long.valueOf(order.getQty()));
            }
            else {
                productAnalytics.setCancelledOrders(1L);
            }
        }

        productAnalyticsRepository.save(productAnalytics);

        AnalyticsSummaryEntity summary =
                analyticsSummaryRepository.findById(1L)
                        .orElseGet(() -> {
                            AnalyticsSummaryEntity entity =
                                    new AnalyticsSummaryEntity();

                            entity.setId(1L);
                            entity.setTotalOrders(0L);
                            entity.setConfirmedOrders(0L);
                            entity.setCancelledOrders(0L);
                            entity.setTotalRevenue(0.0);

                            return entity;
                        });

        summary.setTotalOrders(summary.getTotalOrders() + 1);

        if(event.getStatus() == OrderStatus.CONFIRMED){
            summary.setConfirmedOrders(
                    summary.getConfirmedOrders() + 1
            );

            summary.setTotalRevenue(
                    summary.getTotalRevenue()
                            + order.getTotalAmount()
            );
        }
        else{
            summary.setCancelledOrders(
                    summary.getCancelledOrders() + 1
            );
        }

        analyticsSummaryRepository.save(summary);
        redisTemplate.delete("analyticsSummary");
        redisTemplate.delete("allProductAnalytics");
        redisTemplate.delete("getTopProducts");

    }

    public AnalyticsResponse getAnalytics() {
        AnalyticsResponse response;
        Optional<AnalyticsSummaryEntity> summaryEntityBox = analyticsSummaryRepository.findById(1L);
        if(summaryEntityBox.isEmpty()){

            response = new AnalyticsResponse();
            response.setCancelledOrders(0L);
            response.setTotalOrders(0L);
            response.setTotalRevenue(0.0);
            response.setConfirmedOrders(0L);
            return response;
        }

        response = new AnalyticsResponse();
        AnalyticsSummaryEntity summaryEntity = summaryEntityBox.get();

        response.setCancelledOrders(summaryEntity.getCancelledOrders());
        response.setTotalOrders(summaryEntity.getTotalOrders());
        response.setTotalRevenue(summaryEntity.getTotalRevenue());
        response.setConfirmedOrders(summaryEntity.getConfirmedOrders());

        return response;
    }

    public AnalyticsResponse summaryAnalytics() {
        String key = "analyticsSummary";
        AnalyticsResponse response = (AnalyticsResponse) redisTemplate.opsForValue().get(key);
        if(response != null)
            return response;

        AnalyticsResponse response1 = getAnalytics();
        redisTemplate.opsForValue().set(key,response1);
        return response1;
    }

    public List<ProductAnalyticsEntity> getAllProductAnalytics() {

        String key = "allProductAnalytics";
        List<ProductAnalyticsEntity> productAnalyticsEntities = (List<ProductAnalyticsEntity>) redisTemplate.opsForValue().get(key);
        if(productAnalyticsEntities != null)
            return productAnalyticsEntities;


        productAnalyticsEntities = productAnalyticsRepository.findAll();
        redisTemplate.opsForValue().set(key,productAnalyticsEntities);
        return productAnalyticsEntities;
    }

    public List<ProductAnalyticsEntity> getTopProducts(){

        String key = "getTopProducts";

        List<ProductAnalyticsEntity> topProducts = (List<ProductAnalyticsEntity>) redisTemplate.opsForValue().get(key);
        if(topProducts != null)
            return topProducts;
        topProducts = productAnalyticsRepository.findAllByOrderByRevenueGeneratedDesc()
                .stream()
                .limit(5)
                .toList();

        redisTemplate.opsForValue().set(key,topProducts);
        return topProducts;
    }

    public Double getSuccessRate() {

        AnalyticsSummaryEntity summary =
                analyticsSummaryRepository.findById(1L)
                        .orElse(null);

        if (summary == null || summary.getTotalOrders() == 0)
            return 0.0;

        return (summary.getConfirmedOrders() * 100.0)
                / summary.getTotalOrders();
    }
}
