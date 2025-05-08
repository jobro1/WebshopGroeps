package com.luxuryproductsholding.api.controller;

import com.luxuryproductsholding.api.dao.OrderDAO;
import com.luxuryproductsholding.api.dao.OrderItemDAO;
import com.luxuryproductsholding.api.dto.OrderDTO;
import com.luxuryproductsholding.api.models.Order;
import com.luxuryproductsholding.api.models.OrderItem;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/orders")
@CrossOrigin(origins = "http://localhost:4200")
public class OrderController {

    private final OrderDAO orderDAO;
    private final OrderItemDAO orderItemDAO;

    public OrderController(OrderDAO orderDAO, OrderItemDAO orderItemDAO) {
        this.orderDAO = orderDAO;
        this.orderItemDAO= orderItemDAO;
    }

    @GetMapping
    public ResponseEntity<List<Order>> getAllOrders() {
        return ResponseEntity.ok(this.orderDAO.getAllOrders());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Order> getOrderById(@PathVariable Long id) {
        return  ResponseEntity.ok(this.orderDAO.getOrderById(id));
    }


    @GetMapping("/orderItems")
    public ResponseEntity<List<OrderItem>> getAllOrderItems() {
        return ResponseEntity.ok(this.orderItemDAO.getAllOrderItems());
    }

    @GetMapping(params = "userId")
    public ResponseEntity<List<Order>> getOrdersByUserId(@RequestParam Long userId) {
        return ResponseEntity.ok(this.orderDAO.getAllOrdersByUserId(userId));
    }

    @PostMapping
    public ResponseEntity<Map<String, String>> createOrder(@RequestBody OrderDTO orderDTO) {
        this.orderDAO.creatOrder(orderDTO);
        Map<String, String> response = new HashMap<>();
        response.put("message", "Created an Order");
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }


}
