package net.ecommerce.order_service;

import net.ecommerce.base_domains.dto.Product;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.List;

@FeignClient(
        name = "inventory-service",
        url = "http://localhost:8084"
)
public interface InventoryFeignClient {
    @GetMapping("/api/products")
    List<Product> getAllProducts();

    @GetMapping("/api/products/{id}")
    Product getProductById(
            @PathVariable Long id
    );
}
