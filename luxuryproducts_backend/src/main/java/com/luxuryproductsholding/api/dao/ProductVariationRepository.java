package com.luxuryproductsholding.api.dao;

import com.luxuryproductsholding.api.models.ProductVariation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProductVariationRepository extends JpaRepository<ProductVariation, Long> {
    Optional<ProductVariation> findProductVariationBySku(String sku);
}
