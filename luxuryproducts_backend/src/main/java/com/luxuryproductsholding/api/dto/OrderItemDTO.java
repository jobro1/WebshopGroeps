package com.luxuryproductsholding.api.dto;

import com.fasterxml.jackson.annotation.JsonAlias;

public class OrderItemDTO {
    public long orderItemId;
    public Integer quantity;
    public Double subtotal;

    @JsonAlias("product_id")
    public long productId;

    @JsonAlias("order_id")
    public long orderId;

    public OrderItemDTO( long orderItemId, Integer quantity, Double subtotal, long productId, long orderId) {
        this.orderItemId = orderItemId;
        this.quantity = quantity;
        this.subtotal = subtotal;
        this.productId = productId;
        this.orderId = orderId;
    }
}
