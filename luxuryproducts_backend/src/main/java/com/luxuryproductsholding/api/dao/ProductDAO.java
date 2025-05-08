package com.luxuryproductsholding.api.dao;

import com.luxuryproductsholding.api.dto.ImageUrlDTO;
import com.luxuryproductsholding.api.dto.ProductDTO;
import com.luxuryproductsholding.api.dto.ProductSpecificationsDTO;
import com.luxuryproductsholding.api.models.ImageUrl;
import com.luxuryproductsholding.api.models.Product;
import com.luxuryproductsholding.api.models.ProductCategory;
import com.luxuryproductsholding.api.models.ProductSpecifications;
import jakarta.transaction.Transactional;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Optional;

@Component
public class ProductDAO {

    private final ProductRepository productRepository;
    private final ProductCategoryRepository productCategoryRepository;
    private final ProductSpecificationsRepository productSpecificationsRepository;

    public ProductDAO(ProductRepository productRepository, ProductCategoryRepository productCategoryRepository,
                      ProductSpecificationsRepository productSpecificationsRepository) {
        this.productRepository = productRepository;
        this.productCategoryRepository = productCategoryRepository;
        this.productSpecificationsRepository = productSpecificationsRepository;
    }

    public List<Product> getAllProducts() {
        List<Product> allProducts =  this.productRepository.findAll();
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

    @Transactional
    public void createProduct(ProductDTO productDTO) {
        Optional<ProductCategory> productCategory = this.productCategoryRepository.findById(productDTO.categoryId);
         if (productCategory.isPresent()) {
            Product product = new Product(
                    productDTO.name,
                    productDTO.manufacturer_code,
                    productDTO.price,
                    productDTO.description,
                    productDTO.brand,
                    productDTO.warranty,
                    productDTO.amount,
                    productCategory.get()
            );

             product = this.productRepository.save(product);

             for (ProductSpecificationsDTO specDTO: productDTO.productSpecificationsDTO) {
                 ProductSpecifications productSpecifications = new ProductSpecifications(specDTO.name, specDTO.value, product);
                  this.productSpecificationsRepository.save(productSpecifications);
             }
             for (ImageUrlDTO imageUrlDTO: productDTO.imageUrlDTO) {
                 ImageUrl imageUrl = new ImageUrl(imageUrlDTO.imageUrl, product);
             }
             return;

         }
        throw new ResponseStatusException(
                HttpStatus.NOT_FOUND, "coudn't create product"
        );

    }

}
