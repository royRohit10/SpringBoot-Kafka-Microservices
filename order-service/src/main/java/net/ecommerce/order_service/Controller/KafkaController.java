package net.ecommerce.order_service.Controller;

import net.ecommerce.base_domains.dto.Order;
import net.ecommerce.base_domains.dto.Product;
import net.ecommerce.order_service.InventoryFeignClient;
import net.ecommerce.order_service.service.OrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class KafkaController {

    @Autowired
    private OrderService orderService;

    @Autowired
    private InventoryFeignClient feignClient;


    @PostMapping("/order")
    public ResponseEntity<String> placeOrder(@RequestBody Order order){

        orderService.placeOrder(order);
        return ResponseEntity.ok("Order has been placed");
    }

    @GetMapping("/products")
    public ResponseEntity<List<Product>> getAllProducts(){
        return ResponseEntity.ok(orderService.getAllProducts());
    }

    @GetMapping("/products/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Long id){

        return ResponseEntity.ok(orderService.getProductsById(id));
    }
}
