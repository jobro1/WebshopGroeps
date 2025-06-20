package com.luxuryproductsholding.api.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import java.util.List;

@Entity
@Table(name = "product")
public class Product {
    @Id
    @GeneratedValue
    private Long product_id;

    private String name;
    private Double price;
    private String description;
    private String brand;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JsonIgnore
    private ProductCategory productCategory;

    @OneToMany(mappedBy = "product", cascade = CascadeType.MERGE)
    @JsonManagedReference(value = "product-variation")
    private List<ProductVariation> variations;

    public Product() {}

    public Product(String name, Double price, String description, String brand, ProductCategory productCategory) {
        this.name = name;
        this.price = price;
        this.description = description;
        this.brand = brand;
        this.productCategory = productCategory;
    }

    public ProductCategory getProductCategory() {
        return productCategory;
    }

    public Long getProduct_id() {
        return product_id;
    }

    public String getName() {
        return name;
    }

    public Double getPrice() {
        return price;
    }

    public String getDescription() {
        return description;
    }

    public String getBrand() {
        return brand;
    }

    public ProductCategory getCategory() {
        return productCategory;
    }

    public List<ProductVariation> getVariations() {
        return variations;
    }

    public void setProduct_id(Long product_id) {
        this.product_id = product_id;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public void setCategory(ProductCategory productCategory) {
        this.productCategory = productCategory;
    }

    public void setVariations(List<ProductVariation> variations) {
        this.variations = variations;
    }

    public Long getId() {
        return this.product_id;
    }
}
