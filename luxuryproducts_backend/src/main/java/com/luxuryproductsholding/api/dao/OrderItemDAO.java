package com.luxuryproductsholding.api.dao;

import com.luxuryproductsholding.api.models.OrderItem;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class OrderItemDAO {

    private final ProductRepository productRepository;
    private final OrderItemRepository orderItemRepository;
    private final OrderRepository orderRepository;

    public OrderItemDAO(ProductRepository productRepository, OrderItemRepository orderItemRepository, OrderRepository orderRepository) {
        this.productRepository = productRepository;
        this.orderItemRepository = orderItemRepository;
        this.orderRepository = orderRepository;
    }

    public List<OrderItem> getAllOrderItems() {
        return this.orderItemRepository.findAll();
    }

}
