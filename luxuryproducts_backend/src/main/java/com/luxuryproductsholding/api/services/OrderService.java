package com.luxuryproductsholding.api.services;

import com.luxuryproductsholding.api.dao.OrderItemRepository;
import com.luxuryproductsholding.api.dao.ProductVariationRepository;
import com.luxuryproductsholding.api.dto.OrderDTO;
import com.luxuryproductsholding.api.dto.OrderItemDTO;
import com.luxuryproductsholding.api.exceptions.insufficientStockException;
import com.luxuryproductsholding.api.models.Order;
import com.luxuryproductsholding.api.models.OrderItem;
import com.luxuryproductsholding.api.models.ProductVariation;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.Optional;

@Service
public class OrderService {

    private final ProductVariationRepository productVariationRepository;
    private final OrderItemRepository orderItemRepository;

    public OrderService(ProductVariationRepository productVariationRepository, OrderItemRepository orderItemRepository) {
        this.productVariationRepository = productVariationRepository;
        this.orderItemRepository = orderItemRepository;
    }


    public void saveOrderItems(OrderDTO orderDTO, Order newOrder) {
        for (OrderItemDTO orderItemDTO : orderDTO.orderItems) {
            Optional<ProductVariation> productVariationOptional = this.productVariationRepository.findProductVariationBySku(orderItemDTO.sku);

            if (productVariationOptional.isEmpty()) {
                throw new ResponseStatusException(HttpStatus.NOT_FOUND, "ProductVariation not found with sku: " + orderItemDTO.sku);
            }

            ProductVariation productVariation = productVariationOptional.get();

            if (this.hasEnoughStock(productVariation, orderItemDTO)) {
                this.updateStock(productVariation, orderItemDTO);
                OrderItem orderItem = new OrderItem(orderItemDTO.quantity, orderItemDTO.subtotal, newOrder, productVariation);
                this.orderItemRepository.save(orderItem);
            }
        }

    }

    private void updateStock(ProductVariation productVariation, OrderItemDTO orderItemDTO) {
        int updatedStock = productVariation.getStock() - orderItemDTO.quantity;
        productVariation.setStock(updatedStock);
        this.productVariationRepository.save(productVariation);
    }

    private boolean hasEnoughStock(ProductVariation productVariation, OrderItemDTO orderItemDTO) {
        if (orderItemDTO.quantity <= 0) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "Quantity must be greater than zero.");
        }

        if (productVariation.getStock() < orderItemDTO.quantity) {
            throw new insufficientStockException(
                    "you tried ordering " + productVariation.getSku() + " with " + orderItemDTO.quantity + " while stock is only " + productVariation.getStock()
            );
        }
        return true;
    }

}
