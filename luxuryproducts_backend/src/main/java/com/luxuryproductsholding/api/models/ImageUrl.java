package com.luxuryproductsholding.api.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;

@Entity
public class ImageUrl {
    @Id
    @GeneratedValue
    private long imageId;
    private String imageUrl;

    @ManyToOne(cascade = CascadeType.MERGE)
    @JsonBackReference
    private Product product;

    public ImageUrl() {}
    public ImageUrl(String imageUrl, Product product) {
        this.imageUrl = imageUrl;
        this.product = product;
    }

    public long getImageId() {
        return imageId;
    }

    public void setImageId(long imageId) {
        this.imageId = imageId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Product getProduct() {
        return product;
    }

    public void setProduct(Product product) {
        this.product = product;
    }
}
