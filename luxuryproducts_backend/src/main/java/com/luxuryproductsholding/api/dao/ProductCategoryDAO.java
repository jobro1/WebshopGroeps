package com.luxuryproductsholding.api.dao;

import com.luxuryproductsholding.api.dto.CategoryDTO;
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

    public CategoryDTO getProductCategoryById(Long id) {
        ProductCategory category = this.productCategoryRepository.findByCategoryId(id).get();
        return new CategoryDTO(
                category.getCategoryId(),
                category.getName(),
                category.getProducts()
        );
    }
}
