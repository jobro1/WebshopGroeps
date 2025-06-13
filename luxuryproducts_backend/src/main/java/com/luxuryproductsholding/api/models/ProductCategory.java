package com.luxuryproductsholding.api.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import java.util.List;


@Entity
public class ProductCategory {
    @Id
    @GeneratedValue
    private Long categoryId;
    private String name;

    @OneToMany( mappedBy = "productCategory")
//    @JsonManagedReference(value = "category-product")
    @JsonIgnore
    private List<Product> products;

    public ProductCategory() {}
    public ProductCategory(String name) {this.name = name;}

    public Long getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(Long categoryId) {
        this.categoryId = categoryId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public List<Product> getProducts() {
        return products;
    }

    public void setProducts(List<Product> products) {
        this.products = products;
    }
}
