package com.luxuryproductsholding.api.dao;

import com.luxuryproductsholding.api.models.ProductSpecifications;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ProductSpecificationsDAO {

    private final ProductSpecificationsRepository productSpecificationsRepository;
    private final ProductRepository productRepository;

    public ProductSpecificationsDAO(ProductSpecificationsRepository productSpecificationsRepository, ProductRepository productRepository) {
        this.productSpecificationsRepository = productSpecificationsRepository;
        this.productRepository = productRepository;
    }

    public List<ProductSpecifications> getAllSpecifications() {
        return productSpecificationsRepository.findAll();
    }
}
