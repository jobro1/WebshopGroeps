package com.luxuryproductsholding.api.dao;

import com.luxuryproductsholding.api.models.ImageUrl;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ImageUrlRepository extends JpaRepository<ImageUrl, Long> {
}
