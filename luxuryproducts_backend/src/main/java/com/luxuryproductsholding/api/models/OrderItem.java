package com.luxuryproductsholding.api.models;

import com.fasterxml.jackson.annotation.*;
import jakarta.persistence.*;

@Entity
@JsonIdentityInfo(generator = ObjectIdGenerators.PropertyGenerator.class, property = "orderItemId")

public class OrderItem {
    @Id
    @GeneratedValue
    private Long orderItemId;

    private Integer quantity;
    private Double subtotal;

    @ManyToOne(cascade = CascadeType.MERGE)
    private ProductVariation productVariation;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JsonBackReference
    private Order order;

    public OrderItem() {}

    public OrderItem(Integer quantity, Double subtotal, Order order, ProductVariation productVariation) {
        this.quantity = quantity;
        this.subtotal = subtotal;
        this.order = order;
        this.productVariation = productVariation;
    }

    public Long getOrderItemId() {
        return orderItemId;
    }

    public void setOrderItemId(Long orderItemId) {
        this.orderItemId = orderItemId;
    }

    public Integer getQuantity() {
        return quantity;
    }

    public void setQuantity(Integer quantity) {
        this.quantity = quantity;
    }

    public Double getSubtotal() {
        return subtotal;
    }

    public void setSubtotal(Double subtotal) {
        this.subtotal = subtotal;
    }

    public ProductVariation getProductVariation() {
        return productVariation;
    }

    public void setProductVariation(ProductVariation product) {
        this.productVariation = product;
    }

    public Order geOrder() {
        return this.order;
    }

    public void setOrder(Order userOrder) {
        this.order = userOrder;
    }
}
