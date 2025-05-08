package com.luxuryproductsholding.api.dao;

import com.luxuryproductsholding.api.models.Order;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface OrderRepository extends JpaRepository<Order, Long> {
//    List<Order> findByUser(User user);
    Optional<List<Order>> findByUserUserId(Long userId);
}
