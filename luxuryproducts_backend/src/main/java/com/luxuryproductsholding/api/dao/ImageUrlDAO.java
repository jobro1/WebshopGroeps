package com.luxuryproductsholding.api.dao;

import org.springframework.stereotype.Component;

@Component
public class ImageUrlDAO {

    private final ImageUrlRepository imageUrlRepository;
    private final ProductRepository productRepository;


    public ImageUrlDAO(ImageUrlRepository imageUrlRepository, ProductRepository productRepository) {
        this.imageUrlRepository = imageUrlRepository;
        this.productRepository = productRepository;
    }
}
