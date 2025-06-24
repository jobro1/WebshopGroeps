package com.luxuryproductsholding.api.dto;

public class VariationValueDTO {
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
