package com.luxuryproductsholding.api.dto.JsonDTOs;

import java.util.List;

public class JsonData {
    private List<JsonProduct> products;
    public List<JsonProduct> getProducts() { return products; }
    public void setProducts(List<JsonProduct> products) { this.products = products; }
}
