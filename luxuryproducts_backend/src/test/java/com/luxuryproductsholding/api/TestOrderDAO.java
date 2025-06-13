package com.luxuryproductsholding.api;

import com.luxuryproductsholding.api.dao.OrderDAO;
import com.luxuryproductsholding.api.dao.OrderRepository;
import com.luxuryproductsholding.api.dao.UserRepository;
import com.luxuryproductsholding.api.dto.OrderDTO;
import com.luxuryproductsholding.api.dto.OrderItemDTO;
import com.luxuryproductsholding.api.models.CustomUser;
import com.luxuryproductsholding.api.models.Order;
import com.luxuryproductsholding.api.services.OrderService;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TestOrderDAO {

    @Mock
    private OrderRepository orderRepository;

    @Mock
    private UserRepository userRepository;

    @Mock
    private OrderService orderService;

    @InjectMocks
    private OrderDAO orderDAO;

    @Test
    void testGetAllOrders() {
        List<Order> mockOrders = List.of(new Order(), new Order());
        when(orderRepository.findAll()).thenReturn(mockOrders);

        List<Order> result = orderDAO.getAllOrders();

        assertEquals(2, result.size());
        verify(orderRepository).findAll();
    }

    @Test
    void testGetOrderById() {
        Order mockOrder = new Order();
        when(orderRepository.findById(1L)).thenReturn(Optional.of(mockOrder));

        Order result = orderDAO.getOrderById(1L);

        assertEquals(mockOrder, result);
        verify(orderRepository).findById(1L);
    }

    @Test
    void testGetAllOrdersByUserId_Found() {
        List<Order> mockOrders = List.of(new Order());
        when(orderRepository.findByUserUserId(1L)).thenReturn(Optional.of(mockOrders));

        List<Order> result = orderDAO.getAllOrdersByUserId(1L);

        assertEquals(1, result.size());
        verify(orderRepository).findByUserUserId(1L);
    }

    @Test
    void testGetAllOrdersByUserId_NotFound() {
        when(orderRepository.findByUserUserId(1L)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> orderDAO.getAllOrdersByUserId(1L));

        assertEquals("404 NOT_FOUND \"No orders found with that user id\"", exception.getMessage());
    }

    @Test
    void testCreateOrder_Success() {
        // Arrange
        OrderDTO dto = new OrderDTO();
        dto.userId = 1L;
        dto.totalPrice = 250.00;
        dto.status = "PROCESSING";
        dto.date = "2025-06-12";
        dto.orderItems = List.of(new OrderItemDTO(2, 125.00, "SKu123"));

        CustomUser mockUser = new CustomUser();
        Order mockSavedOrder = new Order(dto.totalPrice, dto.date, dto.status, mockUser);

        when(userRepository.findById(dto.userId)).thenReturn(Optional.of(mockUser));
        when(orderRepository.save(any(Order.class))).thenReturn(mockSavedOrder);
        doNothing().when(orderService).saveOrderItems(dto, mockSavedOrder);

        // Act
        orderDAO.creatOrder(dto);

        // Assert
        ArgumentCaptor<Order> captor = ArgumentCaptor.forClass(Order.class);
        verify(orderRepository).save(captor.capture());
        verify(orderService).saveOrderItems(dto, mockSavedOrder);

        Order createdOrder = captor.getValue();
        assertEquals(dto.totalPrice, createdOrder.getTotalPrice());
        assertEquals(dto.date, createdOrder.getDate());
        assertEquals(dto.status, createdOrder.getStatus());
        assertEquals(mockUser, createdOrder.getUser());
    }

    @Test
    void testCreateOrder_UserNotFound() {
        OrderDTO dto = new OrderDTO();
        dto.userId = 999L;

        when(userRepository.findById(dto.userId)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> orderDAO.creatOrder(dto));

        assertEquals("404 NOT_FOUND \"User not found with ID: 999\"", exception.getMessage());
    }

    @Test
    void testGetOrderById_NotFound() {
        when(orderRepository.findById(99L)).thenReturn(Optional.empty());

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> orderDAO.getOrderById(99L));

        assertEquals("404 NOT_FOUND \"No order found with that id\"", ex.getMessage());
    }


}
