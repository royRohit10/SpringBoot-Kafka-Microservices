package net.ecommerce.checkinventory;

import net.ecommerce.base_domains.dto.Product;
import net.ecommerce.checkinventory.entity.ProductEntity;
import net.ecommerce.checkinventory.repository.ProductRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/products")
public class InventoryController {

    @Autowired
    private KafkaInventoryService kafkaInventoryService;
    @Autowired
    private ProductRepository productRepository;

    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts(){
        return ResponseEntity.ok(kafkaInventoryService.getAllProducts());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Long id){

        return ResponseEntity.ok(kafkaInventoryService.getProductById(id));
    }

    @PostMapping("/addProduct")
    public ResponseEntity<String> addProduct(@RequestBody Product product){

        return ResponseEntity.ok(kafkaInventoryService.addProduct(product));
    }

    @PutMapping("/updateProduct/{id}")
    public ResponseEntity<Product> updateProduct(@RequestBody Product product,@PathVariable Long id){


        return ResponseEntity.ok(kafkaInventoryService.updateProduct(product,id));
    }

    @DeleteMapping("/delete/{id}")
    public ResponseEntity<String> deleteProduct(@PathVariable Long id){

        return ResponseEntity.ok(kafkaInventoryService.deleteProduct(id));
    }
}
