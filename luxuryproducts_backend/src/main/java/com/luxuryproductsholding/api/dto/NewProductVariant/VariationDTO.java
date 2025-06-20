package com.luxuryproductsholding.api.dto.NewProductVariant;

public class VariationDTO {
    public String variationName;
    public String value;

    public VariationDTO(String variationName, String value) {
        this.variationName = variationName;
        this.value = value;
    }
}
