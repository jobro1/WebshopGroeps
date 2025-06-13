package com.luxuryproductsholding.api.dto;

import com.fasterxml.jackson.annotation.JsonAlias;

import java.util.List;

public class OrderDTO {
    public Double totalPrice;
    public String date;
    public String status;
    public List<OrderItemDTO> orderItems;

    @JsonAlias("user_id")
    public long userId;

    public OrderDTO(Double totalPrice, String date, String status, long userId, List<OrderItemDTO> orderItems) {
        this.totalPrice = totalPrice;
        this.date = date;
        this.status = status;
        this.userId = userId;
        this.orderItems = orderItems;
    }

    public OrderDTO() {

    }
}
