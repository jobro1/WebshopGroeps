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

    private String sku;
    private Double priceAtOrder;
    private String variationSummary;
    private String imageUrl;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JsonBackReference
    private Order order;

    public OrderItem() {}

    public OrderItem(Integer quantity, Double subtotal, String sku, Double priceAtOrder, String variationSummary, Order order, String imageUrl) {
        this.quantity = quantity;
        this.subtotal = subtotal;
        this.sku = sku;
        this.priceAtOrder = priceAtOrder;
        this.variationSummary = variationSummary;
        this.imageUrl = imageUrl;
        this.order = order;
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

    public Order geOrder() {
        return this.order;
    }

    public void setOrder(Order userOrder) {
        this.order = userOrder;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public Double getPriceAtOrder() {
        return priceAtOrder;
    }

    public void setPriceAtOrder(Double priceAtOrder) {
        this.priceAtOrder = priceAtOrder;
    }

    public String getVariationSummary() {
        return variationSummary;
    }

    public void setVariationSummary(String variationSummary) {
        this.variationSummary = variationSummary;
    }

    public String getImageUrlAtOrder() {
        return imageUrl;
    }

    public void setImageUrlAtOrder(String imageUrlAtOrder) {
        this.imageUrl = imageUrlAtOrder;
    }
}
