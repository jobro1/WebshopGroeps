package com.luxuryproductsholding.api.dao;

import com.luxuryproductsholding.api.dto.ProductCategoryDTO;
import com.luxuryproductsholding.api.models.ProductCategory;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ProductCategoryDAO {

    private final ProductCategoryRepository productCategoryRepository;

    public ProductCategoryDAO(ProductCategoryRepository productCategoryRepository) {
        this.productCategoryRepository = productCategoryRepository;
    }
    public List<ProductCategory> getAllProductCategories() {
        return this.productCategoryRepository.findAll();
    }

    public void createProductCategory(ProductCategoryDTO productCategoryDTO) {
        this.productCategoryRepository.save(new ProductCategory(productCategoryDTO.name));
    }

    public ProductCategory getProductCategoryById(Long id) {
        return this.productCategoryRepository.findByCategoryId(id).get();
    }
}
