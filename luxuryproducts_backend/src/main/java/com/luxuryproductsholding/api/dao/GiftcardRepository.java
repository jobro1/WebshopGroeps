package com.luxuryproductsholding.api.dao;

import com.luxuryproductsholding.api.models.Giftcard;
import com.luxuryproductsholding.api.models.Giftcard.GiftcardStatus;
import com.luxuryproductsholding.api.models.CustomUser;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface GiftcardRepository extends JpaRepository<Giftcard, Long> {
    Optional<Giftcard> findByCode(String code);

    List<Giftcard> findByStatus(GiftcardStatus status);

    List<Giftcard> findByRecipientEmailOrLinkedUserEmail(String recipientEmail, String linkedUserEmail);

    List<Giftcard> findByExpirationDateBefore(LocalDateTime date);

    List<Giftcard> findByLinkedUser(CustomUser user);

    @Query("SELECT g FROM Giftcard g WHERE g.status = 'ACTIVE' AND g.expirationDate BETWEEN ?1 AND ?2")
    List<Giftcard> findSoonToExpire(LocalDateTime start, LocalDateTime end);

    boolean existsByCode(String code);
}