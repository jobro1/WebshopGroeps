package com.luxuryproductsholding.api.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import java.util.List;
@Entity(name="UserOrder")
public class Order {
    @Id
    @GeneratedValue
    private Long orderId;
    private Double totalPrice;
    private String date;
    private String status;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JsonBackReference
    private CustomUser user;

    @OneToMany(mappedBy = "order")
    @JsonManagedReference
    private List<OrderItem> orderItems;


    public Order() {}

    public Order(Double totalPrice, String date, String status, CustomUser user) {
        this.totalPrice = totalPrice;
        this.date = date;
        this.status = status;
        this.user = user;

    }

    public Long getOrderId() {
        return orderId;
    }

    public void setOrderId(Long orderId) {
        this.orderId = orderId;
    }

    public Double getTotalPrice() {
        return totalPrice;
    }

    public void setTotalPrice(Double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public CustomUser getUser() {
        return user;
    }

    public void setUser(CustomUser user) {
        this.user = user;
    }

    public List<OrderItem> getOrderItems() {
        return orderItems;
    }

    public void setOrderItems(List<OrderItem> orderItems) {
        this.orderItems = orderItems;
    }
}
