package net.ecommerce.stock_service.Kafka;

import net.ecommerce.base_domains.dto.AnalyticsResponse;
import net.ecommerce.stock_service.entities.ProductAnalyticsEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/analytics")
public class AnalyticsController {

    @Autowired
    private KafkaStockService stockService;

    @GetMapping("/summary")
    public ResponseEntity<AnalyticsResponse> getAnalytics() {
        return ResponseEntity.ok(stockService.summaryAnalytics());
    }

    @GetMapping("/getProductAnalytics")
    public ResponseEntity<List<ProductAnalyticsEntity>> getAllProductsOrdered(){
        return ResponseEntity.ok(stockService.getAllProductAnalytics());
    }

    @GetMapping("/getTopProducts")
    public ResponseEntity<List<ProductAnalyticsEntity>> getTopProducts(){
        return ResponseEntity.ok(stockService.getTopProducts());
    }

    @GetMapping("/success-rate")
    public ResponseEntity<Double> getSuccessRate(){
        return ResponseEntity.ok(stockService.getSuccessRate());
    }
}
