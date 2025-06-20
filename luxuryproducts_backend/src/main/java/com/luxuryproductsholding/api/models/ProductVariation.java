package com.luxuryproductsholding.api.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;

import java.util.List;

@Entity
public class ProductVariation {
    @Id
    @GeneratedValue
    private long productVariationId;
    @Column(unique = true)
    private String sku;
    private Double price;
    private String imageUrl;
    private int stock;

    @ManyToOne
    @JoinColumn(name = "product_id")
    @JsonBackReference(value = "product-variation")
    private Product product;

    @ManyToMany
    @JoinTable(
            name  = "product_variation_value",
            joinColumns = @JoinColumn(name = "product_variation_id"),
            inverseJoinColumns = @JoinColumn(name = "variation_value_id"))
    @JsonManagedReference
    private List<VariationValue> values;

    public ProductVariation() {
    }

    public ProductVariation(String sku, Double price, int stock, String imageUrl, Product product, List<VariationValue> values) {
        this.sku = sku;
        this.price = price;
        this.stock = stock;
        this.imageUrl = imageUrl;
        this.product = product;
        this.values = values;
    }

    public long getProductVariationId() {
        return productVariationId;
    }

    public String getSku() {
        return sku;
    }

    public Double getPrice() {
        return price;
    }

    public int getStock() {
        return stock;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public Product getProduct() {
        return product;
    }

    public List<VariationValue> getValues() {
        return values;
    }

    public void setProductVariationId(long productVariationId) {
        this.productVariationId = productVariationId;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public void setProduct(Product product) {
        this.product = product;
    }

    public void setValues(List<VariationValue> values) {
        this.values = values;
    }
}
