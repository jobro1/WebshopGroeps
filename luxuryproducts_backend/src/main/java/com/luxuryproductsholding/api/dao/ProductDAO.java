package com.luxuryproductsholding.api.dao;

import com.luxuryproductsholding.api.dto.ProductDTO;
import com.luxuryproductsholding.api.models.Product;
import com.luxuryproductsholding.api.models.ProductCategory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Component
public class ProductDAO {

    private final ProductRepository productRepository;
    private final ProductCategoryRepository productCategoryRepository;

    public ProductDAO(ProductRepository productRepository, ProductCategoryRepository productCategoryRepository) {
        this.productRepository = productRepository;
        this.productCategoryRepository = productCategoryRepository;
    }

    public List<Product> getAllProducts() {
        List<Product> allProducts =  this.productRepository.findAll();
        System.out.println(allProducts);
        if (allProducts.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        };
        return allProducts;

    }

    public Product getProductById(Long id) {
        Optional<Product> product = this.productRepository.findById(id);
        if (product.isEmpty()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        };
        return product.get();
    }

    public List<Product> getProductsByCategoryId(long categoryId) {
        Optional<List<Product>> products = this.productRepository.findByProductCategoryCategoryId(categoryId);
        if (products.get().isEmpty()){
            throw new ResponseStatusException(
                    HttpStatus.NOT_FOUND, "No tasks found with that category id"
            );
        }
        return products.get();
    }

    public List<Product> searchProducts(String query) {
        return productRepository.findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(query, query);
    }

    public void createProduct(ProductDTO productDTO) {
        Optional<ProductCategory> productCategory = this.productCategoryRepository.findById(productDTO.categoryId);
         if (productCategory.isPresent()) {
            Product product = new Product(
                    productDTO.name,
                    productDTO.price,
                    productDTO.description,
                    productDTO.brand,
                    productCategory.get()
            );

            product = this.productRepository.save(product);

            return;

         }
        throw new ResponseStatusException(
                HttpStatus.NOT_FOUND, "coudn't create product"
        );

    }

}
