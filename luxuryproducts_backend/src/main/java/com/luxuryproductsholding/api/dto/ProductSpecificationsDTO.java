package com.luxuryproductsholding.api.dto;

import com.fasterxml.jackson.annotation.JsonAlias;

public class ProductSpecificationsDTO {
    public String name;
    public String value;

    @JsonAlias("product_id")
    public long productId;

    public ProductSpecificationsDTO(String name, String value, long productId) {
        this.name = name;
        this.value = value;
        this.productId = productId;
    }
}
