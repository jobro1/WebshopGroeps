package com.luxuryproductsholding.api.dao;

import com.luxuryproductsholding.api.models.VariationValue;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VariationValueRepository extends JpaRepository<VariationValue, Long> {
    Optional<VariationValue> findByValueAndVariation_VariationName(String value, String variationName);
}
