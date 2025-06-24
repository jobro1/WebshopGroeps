package com.luxuryproductsholding.api.controller;

import com.luxuryproductsholding.api.dao.ProductDAO;
import com.luxuryproductsholding.api.dto.ProductDTO;
import com.luxuryproductsholding.api.dto.ProductVariationCreateDTO;
import com.luxuryproductsholding.api.models.Product;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import com.luxuryproductsholding.api.dto.ProductVariationUpdateDTO;
import com.luxuryproductsholding.api.models.ProductVariation;

import java.util.List;


@RestController
@RequestMapping("/products")
public class ProductController {

    private  final ProductDAO productDAO;

    public ProductController(ProductDAO productDAO) {
        this.productDAO = productDAO;
    }

    @GetMapping
    public ResponseEntity<List<Product>> getAllProducts() {
        return ResponseEntity.ok(this.productDAO.getAllProducts());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Product> getProductById(@PathVariable Long id) {
        return ResponseEntity.ok(this.productDAO.getProductById(id));
    }


    @GetMapping(params = "categoryId")
    public ResponseEntity<List<Product>> getAllProductsByCategoryId(@RequestParam Long categoryId) {
        return ResponseEntity.ok(this.productDAO.getProductsByCategoryId(categoryId));
    }

    @GetMapping("/search")
    public ResponseEntity<List<Product>> searchProducts(@RequestParam("query") String query) {
        List<Product> products = productDAO.searchProducts(query);
        return ResponseEntity.ok(products);
    }

    @PostMapping
    public ResponseEntity<String> CreateProduct(@RequestBody ProductDTO productDTO) {
    this.productDAO.createProduct(productDTO);
        return ResponseEntity.ok("Created a Product");
    }

    @PutMapping("/admin/variation")
    public ProductVariation updateProductVariation(@RequestBody ProductVariationUpdateDTO dto) {
        return productDAO.updateProductVariation(dto);
    }

    @DeleteMapping("/admin/variation/{id}")
    public ResponseEntity<Void> deleteProductVariation(@PathVariable Long id) {
        productDAO.deleteProductVariation(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/admin/variation/create")
    public ResponseEntity<ProductVariation> createProductVariation(@RequestBody ProductVariationCreateDTO dto) {
        ProductVariation created = productDAO.createProductVariation(dto);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }
}
