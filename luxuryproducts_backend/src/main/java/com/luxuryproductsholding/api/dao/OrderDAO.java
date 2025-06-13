package com.luxuryproductsholding.api.dao;

import com.luxuryproductsholding.api.dto.OrderDTO;
import com.luxuryproductsholding.api.models.*;
import com.luxuryproductsholding.api.services.OrderService;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Component
public class OrderDAO {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final OrderService orderService;

    public OrderDAO(OrderRepository orderRepository, UserRepository userRepository, OrderService orderService) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.orderService = orderService;
    }

    public List<Order> getAllOrders() {
        return this.orderRepository.findAll();
    }

    public Order getOrderById(Long id) {
        return orderRepository.findById(id)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "No order found with that id"));
    }

    public List<Order> getAllOrdersByUserId(Long userId) {
        Optional<List<Order>> orders = this.orderRepository.findByUserUserId(userId);

        if (orders.isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "No orders found with that user id");
        }
        return orders.get();


    }

    public void creatOrder(OrderDTO orderDTO) {
        Optional<CustomUser> userOptional = this.userRepository.findById(orderDTO.userId);

        if (userOptional.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found with ID: " + orderDTO.userId);
        }

        if (orderDTO.orderItems == null || orderDTO.orderItems.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Order must contain at least one item.");
        }

        CustomUser user = userOptional.get();
        Order newOrder = new Order(
                orderDTO.totalPrice,
                orderDTO.date,
                orderDTO.status,
                user
        );
        newOrder = this.orderRepository.save(newOrder);

        this.orderService.saveOrderItems(orderDTO, newOrder);

    }


}
