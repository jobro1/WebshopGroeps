package com.luxuryproductsholding.api.dto;

import java.util.List;

public class ProductVariationCreateDTO {
    public String sku;
    public Double price;
    public int stock;
    public String imageUrl;
    public Long productId;
    public List<VariationValueDTO> values;

    public static class VariationValueDTO {
        public Long variationValueId;
        public String value;
        public String variationName;
    }
}

