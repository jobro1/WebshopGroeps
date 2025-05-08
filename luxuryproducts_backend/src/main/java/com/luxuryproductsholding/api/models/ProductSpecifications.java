package com.luxuryproductsholding.api.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

@Entity
public class ProductSpecifications {

    @Id
    @GeneratedValue
    private Long specId;
    private String name;
    private String value;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JsonBackReference
    private Product product;

    public ProductSpecifications() {}

    public ProductSpecifications(String name, String value, Product product) {
        this.name = name;
        this.value = value;
        this.product = product;
    }

    public Long getSpecId() {
        return specId;
    }

    public void setSpecId(Long specId) {
        this.specId = specId;
    }

    public String getName() {
        return name;
    }

    public void setName(String specName) {
        this.name = specName;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String specValue) {
        this.value = specValue;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }
}
