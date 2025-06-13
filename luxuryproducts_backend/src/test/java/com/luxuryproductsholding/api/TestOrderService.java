package com.luxuryproductsholding.api;

import com.luxuryproductsholding.api.dao.OrderItemRepository;
import com.luxuryproductsholding.api.dao.ProductVariationRepository;
import com.luxuryproductsholding.api.dto.OrderDTO;
import com.luxuryproductsholding.api.dto.OrderItemDTO;
import com.luxuryproductsholding.api.exceptions.insufficientStockException;
import com.luxuryproductsholding.api.models.Order;
import com.luxuryproductsholding.api.models.OrderItem;
import com.luxuryproductsholding.api.models.ProductVariation;
import com.luxuryproductsholding.api.services.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;

import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class TestOrderService {

    @Mock
    private ProductVariationRepository productVariationRepository;

    @Mock
    private OrderItemRepository orderItemRepository;

    @InjectMocks
    private OrderService orderService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSaveOrderItems_Success() {
        OrderDTO dto = new OrderDTO();
        dto.orderItems = List.of(new OrderItemDTO(2, 100.0, "SKU123"));

        ProductVariation pv = new ProductVariation();
        pv.setSku("SKU123");
        pv.setStock(5);

        Order order = new Order();

        when(productVariationRepository.findProductVariationBySku("SKU123"))
                .thenReturn(Optional.of(pv));

        orderService.saveOrderItems(dto, order);

        verify(orderItemRepository).save(any(OrderItem.class));
        verify(productVariationRepository).save(pv);
        assertEquals(3, pv.getStock());
    }

    @Test
    void testSaveOrderItems_ProductVariationNotFound() {
        OrderDTO dto = new OrderDTO();
        dto.orderItems = List.of(new OrderItemDTO(1, 50.0, "UNKNOWN"));

        when(productVariationRepository.findProductVariationBySku("UNKNOWN"))
                .thenReturn(Optional.empty());

        Order order = new Order();

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> orderService.saveOrderItems(dto, order));

        assertTrue(ex.getMessage().contains("ProductVariation not found with sku"));
    }

    @Test
    void testSaveOrderItems_InsufficientStock() {
        OrderDTO dto = new OrderDTO();
        dto.orderItems = List.of(new OrderItemDTO(10, 100.0, "SKU_LOW"));

        ProductVariation pv = new ProductVariation();
        pv.setSku("SKU_LOW");
        pv.setStock(5);

        when(productVariationRepository.findProductVariationBySku("SKU_LOW"))
                .thenReturn(Optional.of(pv));

        Order order = new Order();

        insufficientStockException ex = assertThrows(insufficientStockException.class,
                () -> orderService.saveOrderItems(dto, order));

        assertTrue(ex.getMessage().contains("SKU_LOW"));
    }

    @Test
    void testSaveOrderItems_ExactStockMatch() {
        OrderDTO dto = new OrderDTO();
        dto.orderItems = List.of(new OrderItemDTO(3, 150.0, "SKU_MATCH"));

        ProductVariation pv = new ProductVariation();
        pv.setSku("SKU_MATCH");
        pv.setStock(3);

        when(productVariationRepository.findProductVariationBySku("SKU_MATCH"))
                .thenReturn(Optional.of(pv));

        Order order = new Order();

        orderService.saveOrderItems(dto, order);

        verify(orderItemRepository).save(any(OrderItem.class));
        assertEquals(0, pv.getStock());
    }

    @Test
    void testSaveOrderItems_NegativeQuantity() {
        OrderDTO dto = new OrderDTO();
        dto.orderItems = List.of(new OrderItemDTO(-1, -50.0, "SKU_NEG"));

        ProductVariation pv = new ProductVariation();
        pv.setSku("SKU_NEG");
        pv.setStock(10);

        when(productVariationRepository.findProductVariationBySku("SKU_NEG"))
                .thenReturn(Optional.of(pv));

        Order order = new Order();

        ResponseStatusException ex = assertThrows(ResponseStatusException.class,
                () -> orderService.saveOrderItems(dto, order));

        assertTrue(ex.getMessage().contains("Quantity must be greater than zero"));
    }


    @Test
    void testSaveOrderItems_EmptyOrderItems() {
        OrderDTO dto = new OrderDTO();
        dto.orderItems = List.of(); // empty list

        Order order = new Order();

        assertDoesNotThrow(() -> orderService.saveOrderItems(dto, order));
        verify(orderItemRepository, never()).save(any());
        verify(productVariationRepository, never()).save(any());
    }

    @Test
    void testSaveOrderItems_DuplicateSKUs() {
        OrderDTO dto = new OrderDTO();
        dto.orderItems = List.of(
                new OrderItemDTO(1, 50.0, "SKU_DUP"),
                new OrderItemDTO(2, 100.0, "SKU_DUP")
        );

        ProductVariation pv = new ProductVariation();
        pv.setSku("SKU_DUP");
        pv.setStock(5);

        when(productVariationRepository.findProductVariationBySku("SKU_DUP"))
                .thenReturn(Optional.of(pv));

        Order order = new Order();

        orderService.saveOrderItems(dto, order);

        verify(orderItemRepository, times(2)).save(any(OrderItem.class));
        assertEquals(2, pv.getStock()); // 5 - 1 - 2 = 2
    }
}
