package com.luxuryproductsholding.api.dao;

import com.luxuryproductsholding.api.dto.OrderDTO;
import com.luxuryproductsholding.api.dto.OrderItemDTO;
import com.luxuryproductsholding.api.models.Order;
import com.luxuryproductsholding.api.models.OrderItem;
import com.luxuryproductsholding.api.models.Product;
import com.luxuryproductsholding.api.models.CustomUser;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Component
public class OrderDAO {

    private final OrderRepository orderRepository;
    private final UserRepository userRepository;
    private final ProductRepository productRepository;
    private final OrderItemRepository orderItemRepository;

    public OrderDAO(OrderRepository orderRepository, UserRepository userRepository,
                    ProductRepository productRepository, OrderItemRepository orderItemRepository) {
        this.orderRepository = orderRepository;
        this.userRepository = userRepository;
        this.productRepository = productRepository;
        this.orderItemRepository = orderItemRepository;
    }

    public List<Order> getAllOrders() {
        return this.orderRepository.findAll();
    }

    public Order getOrderById(Long id) {
        return this.orderRepository.findById(id).get();
    }

    public List<Order> getAllOrdersByUserId(Long userId) {
        Optional<List<Order>> orders = this.orderRepository.findByUserUserId(userId);

        if (orders.get().isEmpty()) {
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "No orders found with that user id");
        }
        return orders.get();


    }

//    public void creatOrder(OrderDTO orderDTO) {
//        Optional<CustomUser> user = this.userRepository.findById(orderDTO.userId);
//        if (user.isPresent()) {
//            Order order = new Order(
//                    orderDTO.totalPrice,
//                    orderDTO.date,
//                    orderDTO.status,
//                    user.get()
//            );
//            order = this.orderRepository.save(order);
//            for(OrderItemDTO orderItemDTO: orderDTO.orderItemDTO) {
//                Optional<Product> product = this.productRepository.findById(orderItemDTO.productId);
//                OrderItem orderItem = new OrderItem(orderItemDTO.quantity, orderItemDTO.subtotal, order, product.get());
//                this.orderItemRepository.save(orderItem);
//            }
//
//            return;
//        }
//        throw new ResponseStatusException(
//                HttpStatus.NOT_FOUND, "coudn't create order"
//        );
//    }

    public Order creatOrder(OrderDTO orderDTO) {
        Optional<CustomUser> userOptional = this.userRepository.findById(orderDTO.userId);

        if (userOptional.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "User not found with ID: " + orderDTO.userId);
        }

        CustomUser user = userOptional.get();
        Order order = new Order(
                orderDTO.totalPrice,
                orderDTO.date,
                orderDTO.status,
                user
        );
        order = this.orderRepository.save(order);

        for (OrderItemDTO orderItemDTO : orderDTO.orderItems) {
            Optional<Product> productOptional = this.productRepository.findById(orderItemDTO.productId);

            if (productOptional.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found with ID: " + orderItemDTO.productId);
            }

            Product product = productOptional.get();
            OrderItem orderItem = new OrderItem(orderItemDTO.orderItemId, orderItemDTO.quantity, orderItemDTO.subtotal, order, product);
            this.orderItemRepository.save(orderItem);
        }

        return order;
    }
}
