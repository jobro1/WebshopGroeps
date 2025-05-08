package com.luxuryproductsholding.api.models;

import com.fasterxml.jackson.annotation.JsonBackReference;
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
    private String manufacturer_code;
    private Double price;
    private String description;
    private String brand;
    private String warranty;
    private Integer amount;


    @ManyToOne(cascade = CascadeType.MERGE)
    @JsonBackReference
    private ProductCategory productCategory;

    @OneToMany(mappedBy = "product")
    @JsonManagedReference
    private List<ProductSpecifications> productSpecifications;

//    @OneToMany(mappedBy = "product")
//    @JsonManagedReference
//    private List<OrderItem> orderItems;

    @OneToMany(mappedBy= "product")
    @JsonManagedReference
    private List<ImageUrl> imageUrls;



    public Product() {}

    public Product(String name, String manufacturer_code, Double price, String description, String brand,
                   String warranty, Integer amount, ProductCategory productCategory
                   ) {
        this.name = name;
        this.manufacturer_code = manufacturer_code;
        this.price = price;
        this.description = description;
        this.brand = brand;
        this.warranty = warranty;
        this.amount = amount;
        this.productCategory = productCategory;


    }


    public ProductCategory getProductCategory() {
        return productCategory;
    }

    public void setProductCategory(ProductCategory productCategory) {
        this.productCategory = productCategory;
    }

    public Long getProduct_id() {
        return product_id;
    }

    public void setProduct_id(Long product_id) {
        this.product_id = product_id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getManufacturer_code() {
        return manufacturer_code;
    }

    public void setManufacturer_code(String manufacturer_code) {
        this.manufacturer_code = manufacturer_code;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getWarranty() {
        return warranty;
    }

    public void setWarranty(String warranty) {
        this.warranty = warranty;
    }

    public Integer getAmount() {
        return amount;
    }

    public void setAmount(Integer amount) {
        this.amount = amount;
    }


}
