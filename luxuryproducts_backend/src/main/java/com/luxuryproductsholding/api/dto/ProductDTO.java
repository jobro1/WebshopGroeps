package com.luxuryproductsholding.api.dto;

import com.fasterxml.jackson.annotation.JsonAlias;

import java.util.List;

public class ProductDTO {
    public String name;
    public String manufacturer_code;
    public Double price;
    public String description;
    public String brand;
    public String warranty;
    public Integer amount;

    public List<ProductSpecificationsDTO> productSpecificationsDTO;
    public List<ImageUrlDTO> imageUrlDTO;
    @JsonAlias("category_id")
    public long categoryId;

    public ProductDTO(String name, String manufacturer_code, Double price, String description, String brand,
                      String warranty, Integer amount, long categoryId



    ) {
        this.name = name;
        this.manufacturer_code = manufacturer_code;
        this.price = price;
        this.description = description;
        this.brand = brand;
        this.warranty = warranty;
        this.amount = amount;
        this.categoryId = categoryId;

    }
}
