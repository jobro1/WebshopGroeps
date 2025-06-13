package com.luxuryproductsholding.api.dao;

import com.luxuryproductsholding.api.models.Variation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface VariationRepository extends JpaRepository<Variation, Long> {
    Optional<Variation> findByVariationName(String variationName);
}
