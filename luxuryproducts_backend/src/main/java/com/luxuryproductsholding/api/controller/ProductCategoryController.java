package com.luxuryproductsholding.api.controller;


import com.luxuryproductsholding.api.dao.ProductCategoryDAO;
import com.luxuryproductsholding.api.dto.ProductCategoryDTO;
import com.luxuryproductsholding.api.models.ProductCategory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/productCategories")
@CrossOrigin(origins = "http://localhost:4200")
public class ProductCategoryController {

    private final ProductCategoryDAO productCategoryDAO;


    public ProductCategoryController(ProductCategoryDAO productCategoryDAO) {
        this.productCategoryDAO = productCategoryDAO;
    }

    @GetMapping
    public ResponseEntity<List<ProductCategory>> getAllProductCategories() {
       return ResponseEntity.ok(this.productCategoryDAO.getAllProductCategories());
    }
    @GetMapping("/{id}")
    public ResponseEntity<ProductCategory> getproductCategoryById(@PathVariable Long id) {
        return ResponseEntity.ok(this.productCategoryDAO.getProductCategoryById(id));
    }

    @PostMapping
    public ResponseEntity<String> createProductCategory(@RequestBody ProductCategoryDTO productCategoryDTO) {
        this.productCategoryDAO.createProductCategory(productCategoryDTO);
        return ResponseEntity.ok("Created a ProductCategory");
    }

}
