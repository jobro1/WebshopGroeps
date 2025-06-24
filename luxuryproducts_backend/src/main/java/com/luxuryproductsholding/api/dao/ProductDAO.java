package com.luxuryproductsholding.api.dao;

import com.luxuryproductsholding.api.dto.ProductDTO;
import com.luxuryproductsholding.api.dto.ProductVariationCreateDTO;
import com.luxuryproductsholding.api.models.*;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;

import com.luxuryproductsholding.api.dto.ProductVariationUpdateDTO;

import java.util.List;
import java.util.Optional;

@Component
public class ProductDAO {

    private final ProductRepository productRepository;
    private final ProductCategoryRepository productCategoryRepository;
    private final ProductVariationRepository productVariationRepository;
    private final VariationValueRepository variationValueRepository;
    private final VariationRepository variationRepository;

    public ProductDAO(ProductRepository productRepository,
                      ProductCategoryRepository productCategoryRepository,
                      ProductVariationRepository productVariationRepository,
                      VariationValueRepository variationValueRepository,
                      VariationRepository variationRepository) {
        this.productRepository = productRepository;
        this.productCategoryRepository = productCategoryRepository;
        this.productVariationRepository = productVariationRepository;
        this.variationValueRepository = variationValueRepository;
        this.variationRepository = variationRepository;
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

    public ProductVariation updateProductVariation(ProductVariationUpdateDTO dto) {
        Long variationId = dto.productVariationId;
        System.out.println(variationId);

        ProductVariation variation = productVariationRepository.findById(variationId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product variation not found"));

        // Update basic fields
        variation.setSku(dto.sku);
        variation.setPrice(dto.price);
        variation.setStock(dto.stock);
        variation.setImageUrl(dto.imageUrl);

        // Map and update VariationValues
        List<VariationValue> updatedValues = dto.values.stream()
                .map(v -> {
                    VariationValue vv = variationValueRepository.findById(v.getVariationValueId())
                            .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
                                    "VariationValue with ID " + v.getVariationValueId() + " not found"));

                    if (v.getValue() != null && !v.getValue().equals(vv.getValue())) {
                        vv.setValue(v.getValue());
                    }

                    return vv;
                })
                .toList();


        // Replace current values
        variation.getValues().clear();
        variation.getValues().addAll(updatedValues);

        ProductVariation saved = productVariationRepository.save(variation);

        return saved;
    }

    public void deleteProductVariation(Long variationId) {
        ProductVariation variation = productVariationRepository.findById(variationId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product variation not found"));

        // Optional: clear associations to avoid FK constraint issues (depends on your cascade config)
        variation.getValues().clear();
        productVariationRepository.delete(variation);

        System.out.println("Deleted product variation with ID: " + variationId);
    }

    public ProductVariation createProductVariation(ProductVariationCreateDTO dto) {
        Product product = productRepository.findById(dto.productId)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Product not found"));

        List<VariationValue> variationValues = dto.values.stream().map(v -> {
            if (v.variationValueId != null) {
                // Use existing VariationValue
                return variationValueRepository.findById(v.variationValueId)
                        .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "VariationValue not found"));
            } else {
                // Create new Variation and VariationValue
                if (v.variationName == null || v.value == null) {
                    throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "New variation must have name and value");
                }

                // Try to find existing variation by name
                Variation variation = variationRepository.findByVariationName(v.variationName)
                        .orElseGet(() -> {
                            Variation newVar = new Variation();
                            newVar.setName(v.variationName);
                            return variationRepository.save(newVar);
                        });

                // Check if value already exists under this variation
                Optional<VariationValue> existingValue = variationValueRepository
                        .findByValueAndVariation_VariationName(String.valueOf(variation), v.value);

                return existingValue.orElseGet(() -> {
                    VariationValue newVal = new VariationValue();
                    newVal.setVariation(variation);
                    newVal.setValue(v.value);
                    return variationValueRepository.save(newVal);
                });
            }
        }).toList();


        ProductVariation newVariation = new ProductVariation();
        newVariation.setSku(dto.sku);
        newVariation.setPrice(dto.price);
        newVariation.setStock(dto.stock);
        newVariation.setImageUrl(dto.imageUrl);
        newVariation.setProduct(product);
        newVariation.setValues(variationValues);

        return productVariationRepository.save(newVariation);
    }



}