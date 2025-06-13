package com.luxuryproductsholding.api.dto;

import java.util.List;

public class UpdateVariantDTO {
    public String sku;
    public Double price;
    public int stock;
    public List<VariationValueDTO> variations;
}
