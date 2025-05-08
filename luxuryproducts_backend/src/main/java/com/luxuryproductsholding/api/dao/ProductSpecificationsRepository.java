package com.luxuryproductsholding.api.dao;

import com.luxuryproductsholding.api.models.Product;
import com.luxuryproductsholding.api.models.ProductSpecifications;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductSpecificationsRepository extends JpaRepository<ProductSpecifications, Long> {
    List<ProductSpecifications> findByProduct(Product product);
}
