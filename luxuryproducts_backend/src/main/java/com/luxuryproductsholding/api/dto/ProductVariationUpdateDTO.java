package com.luxuryproductsholding.api.dto;

import java.util.List;

public class ProductVariationUpdateDTO {
    public long productVariationId;
    public String sku;
    public Double price;
    public String imageUrl;
    public int stock;

    public List<VariationValueDTO> values;

    public static class VariationValueDTO {
        public Long variationValueId;
        public String value;
        public String variationName;

        public Long getVariationValueId() { return variationValueId; }
        public void setVariationValueId(Long variationValueId) { this.variationValueId = variationValueId; }

        public String getValue() { return value; }
        public void setValue(String value) { this.value = value; }

        public String getVariationName() { return variationName; }
        public void setVariationName(String variationName) { this.variationName = variationName; }
    }

    public long getProductVariationId() {
        return productVariationId;
    }

    public void setProductVariationId(long productVariationId) {
        this.productVariationId = productVariationId;
    }

    public String getSku() {
        return sku;
    }

    public void setSku(String sku) {
        this.sku = sku;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public int getStock() {
        return stock;
    }

    public void setStock(int stock) {
        this.stock = stock;
    }

    public List<VariationValueDTO> getValues() {
        return values;
    }

    public void setValues(List<VariationValueDTO> values) {
        this.values = values;
    }
}
