package net.ecommerce.checkinventory;

import net.ecommerce.base_domains.dto.*;
import net.ecommerce.checkinventory.entity.ProductEntity;
import net.ecommerce.checkinventory.repository.ProductRepository;
import org.springframework.data.redis.core.ReactiveSetOperations;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.List;

@Service
public class KafkaInventoryService {

    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaInventoryService.class);


    @Autowired
    private KafkaSendUpdate kafkaSendUpdate;

    @Autowired
    private RedisTemplate<String,Object> redisTemplate;
    @Autowired
    private ProductRepository productRepository;


    @KafkaListener(topics = "order_request",groupId = "${spring.kafka.consumer.group-id}")
    public void consumeMessage(OrderRequestedEvent event){

        LOGGER.info(String.format("Event received -> %s",event.toString()));
        Order order = event.getOrder();

        Long productId = order.getProductId();
        Integer qty = order.getQty();

        Integer available = getProductById(productId).getAvailableQuantity();

        OrderUpdatedEvent orderUpdatedEvent = new OrderUpdatedEvent();

        if(qty <= available){
            ProductEntity entity = productRepository.findById(productId).orElseThrow();
            entity.setAvailableQuantity(available - qty);

            productRepository.save(entity);
            redisTemplate.delete("product:" + productId);
            redisTemplate.delete("allProducts");
            orderUpdatedEvent.setStatus(OrderStatus.CONFIRMED);
            orderUpdatedEvent.setMessage("You will receive the order within 7 days");
        }
        else {
            orderUpdatedEvent.setStatus(OrderStatus.CANCELLED);
            orderUpdatedEvent.setMessage("Your order has been cancelled due to non-availability");
        }

        orderUpdatedEvent.setOrderId(event.getOrderId());
        orderUpdatedEvent.setOrder(event.getOrder());
        orderUpdatedEvent.setCreatedAt(event.getCreatedAt());
        orderUpdatedEvent.setUpdatedAt(LocalDateTime.now());


        kafkaSendUpdate.sendMessage(orderUpdatedEvent);
    }

    public Product getProductById(Long id){

        String key = "product:" + id;
        Product product = (Product) redisTemplate.opsForValue().get(key);

        if(product != null){
            return product;
        }
        ProductEntity entity =
                productRepository.findById(id)
                        .orElseThrow();

        product = new Product(
                entity.getId(),
                entity.getName(),
                entity.getCategory(),
                entity.getPrice(),
                entity.getAvailableQuantity()
        );

        redisTemplate.opsForValue().set(key,product);
        return product;
    }

    public List<Product> getAllProducts() {

        List<Product> products = (List<Product>) redisTemplate.opsForValue().get("allProducts");
        if (products != null)
            return products;

        List<ProductEntity> entities = productRepository.findAll();

        products = entities.stream()
                .map(entity -> new Product(
                        entity.getId(),
                        entity.getName(),
                        entity.getCategory(),
                        entity.getPrice(),
                        entity.getAvailableQuantity()
                ))
                .toList();

        redisTemplate.opsForValue().set("allProducts", products);
        return products;
    }

    public String addProduct(Product product){

        ProductEntity entity = new ProductEntity();
        entity.setName(product.getName());
        entity.setCategory(product.getCategory());
        entity.setPrice(product.getPrice());
        entity.setAvailableQuantity(product.getAvailableQuantity());

        productRepository.save(entity);
        redisTemplate.delete("allProducts");
        return "Product has been added";
    }


    public Product updateProduct(Product product,Long id){

        ProductEntity entity = productRepository.findById(id).
                orElseThrow(()->new RuntimeException("No product available"));

        entity.setName(product.getName());
        entity.setCategory(product.getCategory());
        entity.setPrice(product.getPrice());
        entity.setAvailableQuantity(product.getAvailableQuantity());

        productRepository.save(entity);
        redisTemplate.delete("allProducts");
        redisTemplate.delete("product:"+id);
        return product;
    }

    public String deleteProduct(Long id){
        String key = "product:" + id;

        redisTemplate.delete("allProducts");
        redisTemplate.delete(key);
        productRepository.deleteById(id);
        return "Product is deleted";
    }
}


