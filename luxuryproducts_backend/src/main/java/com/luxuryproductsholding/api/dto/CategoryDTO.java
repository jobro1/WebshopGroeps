package com.luxuryproductsholding.api.dto;

import com.luxuryproductsholding.api.models.Product;

import java.util.List;

public class CategoryDTO {
    private Long categoryId;
    private String name;
    private List<Product> products;

    public CategoryDTO(Long categoryId, String name, List<Product> products) {
        this.categoryId = categoryId;
        this.name = name;
        this.products = products;
    }

    public Long getCategoryId() {
        return categoryId;
    }

    public String getName() {
        return name;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }
}
