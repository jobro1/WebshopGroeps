package com.luxuryproductsholding.api.dto.NewProductVariant;

import java.util.List;

public class VariantDTO {
    public Long productId;
    public String baseSku;
    public Double price;
    public int stock;
    public List<VariationDTO> variations;
}
