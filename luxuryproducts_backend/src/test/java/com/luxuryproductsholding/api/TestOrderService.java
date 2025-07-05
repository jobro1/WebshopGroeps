package com.luxuryproductsholding.api;

import com.luxuryproductsholding.api.dao.OrderItemRepository;
import com.luxuryproductsholding.api.dao.ProductVariationRepository;
import com.luxuryproductsholding.api.dto.OrderDTO;
import com.luxuryproductsholding.api.dto.OrderItemDTO;
import com.luxuryproductsholding.api.exceptions.insufficientStockException;
import com.luxuryproductsholding.api.models.*;
import com.luxuryproductsholding.api.services.OrderService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;

import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class TestOrderService {

    @Mock
    private ProductVariationRepository productVariationRepository;

    @Mock
    private OrderItemRepository orderItemRepository;

    @InjectMocks
    private OrderService orderService;

    @Test
    void testSaveOrderItems_Success() {
        OrderDTO dto = new OrderDTO();
        dto.orderItems = List.of(new OrderItemDTO(2, 100.0, "SKU123"));

        ProductVariation pv = new ProductVariation();
        pv.setSku("SKU123");
        pv.setStock(5);
        pv.setPrice(50.0);
        pv.setImageUrl("http://example.com/image.jpg");

        // set variation values for summary
        VariationValue vv = new VariationValue();
        vv.setValue("M");
        Variation var = new Variation();
        var.setVariationName("Size");
        vv.setVariation(var);
        pv.setValues(List.of(vv));

        Order order = new Order();

        when(productVariationRepository.findProductVariationBySku("SKU123"))
                .thenReturn(Optional.of(pv));

        orderService.saveOrderItems(dto, order);

        // capture the OrderItem that was saved
        ArgumentCaptor<OrderItem> orderItemCaptor = ArgumentCaptor.forClass(OrderItem.class);
        verify(orderItemRepository).save(orderItemCaptor.capture());

        OrderItem savedItem = orderItemCaptor.getValue();
        assertEquals("SKU123", savedItem.getSku());
        assertEquals(50.0, savedItem.getPriceAtOrder());
        assertEquals("Size: M", savedItem.getVariationSummary());
        assertEquals("http://example.com/image.jpg", savedItem.getImageUrlAtOrder());
        assertEquals(2, savedItem.getQuantity());
        assertEquals(100.0, savedItem.getSubtotal());
        assertEquals(order, savedItem.geOrder());

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
        pv.setPrice(50.0);
        pv.setImageUrl("http://example.com/img.png");

        // set variation values for summary
        VariationValue vv = new VariationValue();
        vv.setValue("Red");
        Variation var = new Variation();
        var.setVariationName("Color");
        vv.setVariation(var);
        pv.setValues(List.of(vv));

        when(productVariationRepository.findProductVariationBySku("SKU_MATCH"))
                .thenReturn(Optional.of(pv));

        Order order = new Order();

        orderService.saveOrderItems(dto, order);

        // capture and assert the OrderItem saved
        ArgumentCaptor<OrderItem> orderItemCaptor = ArgumentCaptor.forClass(OrderItem.class);
        verify(orderItemRepository).save(orderItemCaptor.capture());

        OrderItem savedItem = orderItemCaptor.getValue();
        assertEquals("SKU_MATCH", savedItem.getSku());
        assertEquals(50.0, savedItem.getPriceAtOrder());
        assertEquals("Color: Red", savedItem.getVariationSummary());
        assertEquals("http://example.com/img.png", savedItem.getImageUrlAtOrder());
        assertEquals(3, savedItem.getQuantity());
        assertEquals(150.0, savedItem.getSubtotal());
        assertEquals(order, savedItem.geOrder());

        // assert stock now zero
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

        assertTrue(ex.getMessage().contains("Quantity must be greater than zero"),
                "Expected error message not found");
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
        pv.setPrice(50.0);
        pv.setImageUrl("http://example.com/dup.png");

        VariationValue vv = new VariationValue();
        vv.setValue("Large");
        Variation var = new Variation();
        var.setVariationName("Size");
        vv.setVariation(var);
        pv.setValues(List.of(vv));

        when(productVariationRepository.findProductVariationBySku("SKU_DUP"))
                .thenReturn(Optional.of(pv));

        Order order = new Order();

        orderService.saveOrderItems(dto, order);

        // capture the two OrderItems saved
        ArgumentCaptor<OrderItem> captor = ArgumentCaptor.forClass(OrderItem.class);
        verify(orderItemRepository, times(2)).save(captor.capture());

        List<OrderItem> savedItems = captor.getAllValues();

        // first item
        OrderItem item1 = savedItems.get(0);
        assertEquals("SKU_DUP", item1.getSku());
        assertEquals(50.0, item1.getPriceAtOrder());
        assertEquals("Size: Large", item1.getVariationSummary());
        assertEquals("http://example.com/dup.png", item1.getImageUrlAtOrder());
        assertEquals(1, item1.getQuantity());
        assertEquals(50.0, item1.getSubtotal());
        assertEquals(order, item1.geOrder());

        // second item
        OrderItem item2 = savedItems.get(1);
        assertEquals("SKU_DUP", item2.getSku());
        assertEquals(50.0, item2.getPriceAtOrder());
        assertEquals("Size: Large", item2.getVariationSummary());
        assertEquals("http://example.com/dup.png", item2.getImageUrlAtOrder());
        assertEquals(2, item2.getQuantity());
        assertEquals(100.0, item2.getSubtotal());
        assertEquals(order, item2.geOrder());

        // final stock check
        assertEquals(2, pv.getStock()); // 5 -1 -2 = 2
    }

}
