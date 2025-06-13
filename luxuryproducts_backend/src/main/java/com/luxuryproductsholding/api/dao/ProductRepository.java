package com.luxuryproductsholding.api.dao;

import com.luxuryproductsholding.api.models.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;


@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    Optional<List<Product>> findByProductCategoryCategoryId(Long categoryId);
    Optional<Product> findById(Long id);
    List<Product> findByNameContainingIgnoreCaseOrDescriptionContainingIgnoreCase(String name, String description);


    Product findByName(String name);

}
