package com.luxuryproductsholding.api.dto;

import com.fasterxml.jackson.annotation.JsonAlias;

public class OrderItemDTO {
    public Integer quantity;
    public Double subtotal;

    @JsonAlias("product_id")
    public String sku;

    public OrderItemDTO(Integer quantity, Double subtotal, String sku) {
        this.quantity = quantity;
        this.subtotal = subtotal;
        this.sku = sku;
    }


}
