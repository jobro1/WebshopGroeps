package com.luxuryproductsholding.api.dto;

import com.fasterxml.jackson.annotation.JsonAlias;

public class ImageUrlDTO {
    public String imageUrl;

    @JsonAlias("imageUrl_id")
    public long productId;

    public ImageUrlDTO(String imageUrl, long productId) {
        this.imageUrl = imageUrl;
        this.productId = productId;
    }
}
