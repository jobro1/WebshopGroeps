package com.luxuryproductsholding.api.dto;

import com.fasterxml.jackson.annotation.JsonAlias;

import java.util.List;

public class ProductDTO {
    public String name;
    public Double price;
    public String description;
    public String brand;

    @JsonAlias("category_id")
    public long categoryId;

    public ProductDTO(String name, Double price, String description, String brand, long categoryId) {
        this.name = name;
        this.price = price;
        this.description = description;
        this.brand = brand;
        this.categoryId = categoryId;
    }
}
