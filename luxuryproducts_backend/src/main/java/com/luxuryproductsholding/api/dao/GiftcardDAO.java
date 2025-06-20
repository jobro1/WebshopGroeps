package com.luxuryproductsholding.api.dao;

import com.luxuryproductsholding.api.models.Giftcard;
import com.luxuryproductsholding.api.models.CustomUser;
import com.luxuryproductsholding.api.dto.CreateGiftcardRequest;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

@Component
public class GiftcardDAO {
    private static final String CODE_PREFIX = "LPH";
    private static final int CODE_LENGTH = 12;
    private static final String CODE_CHARS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final int MAX_ATTEMPTS_PER_HOUR = 5;
    private static final BigDecimal[] VALID_AMOUNTS = {
            new BigDecimal("50.00"),
            new BigDecimal("100.00"),
            new BigDecimal("200.00"),
            new BigDecimal("400.00"),
            new BigDecimal("780.00")
    };

    private final GiftcardRepository GiftcardRepository;
    private final UserDAO userDAO;
    private final Random random = new Random();

    public GiftcardDAO(GiftcardRepository GiftcardRepository, UserDAO UserDao) {
        this.GiftcardRepository = GiftcardRepository;
        this.userDAO = UserDao;
    }

    @Transactional
    public Giftcard createGiftcard(CreateGiftcardRequest request) {
        validateAmount(request.getValue());

        Giftcard Giftcard = new Giftcard();
        Giftcard.setLinkedUser(this.userDAO.getUserByEmail(request.getRecipientEmail()));
        Giftcard.setCode(generateUniqueCode());
        Giftcard.setInitialValue(request.getValue());
        Giftcard.setCurrentBalance(request.getValue());
        Giftcard.setMessage(request.getMessage());
        Giftcard.setRecipientEmail(request.getRecipientEmail());
        Giftcard.setStatus(com.luxuryproductsholding.api.models.Giftcard.GiftcardStatus.ACTIVE);
        Giftcard.setCreatedAt(LocalDateTime.now());
        Giftcard.setExpirationDate(LocalDateTime.now().plusYears(1));

        return GiftcardRepository.save(Giftcard);
    }

    @Transactional
    public Giftcard linkToUser(String code, CustomUser user) {
        Giftcard Giftcard = findByCode(code);
        validateGiftcardStatus(Giftcard);
        validateAttempts(Giftcard);

        if (Giftcard.getLinkedUser() != null && !Giftcard.getLinkedUser().getUserId().equals(user.getUserId())) {
            throw new IllegalStateException("Gift card is already linked to another user");
        }

        Giftcard.setLinkedUser(user);
        Giftcard.setActivatedAt(LocalDateTime.now());
        return GiftcardRepository.save(Giftcard);
    }

    @Transactional
    public void useGiftcard(String code, BigDecimal amount, CustomUser user) {
        Giftcard Giftcard = findByCode(code);
        validateGiftcardStatus(Giftcard);
        validateUserLink(Giftcard, user);

        if (Giftcard.getCurrentBalance().compareTo(amount) < 0) {
            throw new IllegalStateException("Insufficient balance on gift card");
        }

        Giftcard.setCurrentBalance(Giftcard.getCurrentBalance().subtract(amount));
        GiftcardRepository.save(Giftcard);
    }

    @Transactional
    public Giftcard updateBalance(String code, BigDecimal newBalance, CustomUser user) {
        Giftcard giftcard = findByCode(code);
        validateGiftcardStatus(giftcard);
        validateUserLink(giftcard, user);

        if (newBalance.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Gift card balance cannot be negative");
        }

        if (newBalance.compareTo(giftcard.getCurrentBalance()) > 0) {
            throw new IllegalArgumentException("New balance cannot be greater than current balance");
        }

        giftcard.setCurrentBalance(newBalance);
        if (newBalance.compareTo(BigDecimal.ZERO) == 0) {
            giftcard.setStatus(Giftcard.GiftcardStatus.USED);
        }

        return GiftcardRepository.save(giftcard);
    }

    public List<Giftcard> searchGiftcards(String status, String email, Boolean expired) {
        if (status != null) {
            return GiftcardRepository.findByStatus(Giftcard.GiftcardStatus.valueOf(status));
        }
        if (email != null) {
            return GiftcardRepository.findByRecipientEmailOrLinkedUserEmail(email, email);
        }
        if (expired != null && expired) {
            return GiftcardRepository.findByExpirationDateBefore(LocalDateTime.now());
        }
        return GiftcardRepository.findAll();
    }

    public List<Giftcard> getGiftcardsForUser(CustomUser user) {
        return GiftcardRepository.findByLinkedUser(user);
    }

    private Giftcard findByCode(String code) {
        return GiftcardRepository.findByCode(code)
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND, "Gift card not found"));
    }

    private String generateUniqueCode() {
        StringBuilder code = new StringBuilder(CODE_PREFIX);
        int remainingLength = CODE_LENGTH - CODE_PREFIX.length();

        for (int i = 0; i < remainingLength; i++) {
            int index = random.nextInt(CODE_CHARS.length());
            code.append(CODE_CHARS.charAt(index));
        }

        return code.toString();
    }

    private void validateAmount(BigDecimal amount) {
        boolean isValid = false;
        for (BigDecimal validAmount : VALID_AMOUNTS) {
            if (validAmount.compareTo(amount) == 0) {
                isValid = true;
                break;
            }
        }

        if (!isValid) {
            throw new IllegalArgumentException("Invalid gift card amount");
        }
    }

    private void validateGiftcardStatus(Giftcard Giftcard) {
        if (Giftcard.getStatus() != com.luxuryproductsholding.api.models.Giftcard.GiftcardStatus.ACTIVE) {
            throw new IllegalStateException("Gift card is not active");
        }

        if (Giftcard.getExpirationDate().isBefore(LocalDateTime.now())) {
            Giftcard.setStatus(com.luxuryproductsholding.api.models.Giftcard.GiftcardStatus.EXPIRED);
            GiftcardRepository.save(Giftcard);
            throw new IllegalStateException("Gift card has expired");
        }
    }

    private void validateAttempts(Giftcard Giftcard) {
        if (Giftcard.getFailedAttempts() >= MAX_ATTEMPTS_PER_HOUR) {
            LocalDateTime oneHourAgo = LocalDateTime.now().minusHours(1);
            if (Giftcard.getLastFailedAttempt().isAfter(oneHourAgo)) {
                throw new IllegalStateException("Too many failed attempts. Please try again later");
            } else {
                Giftcard.setFailedAttempts(0);
                GiftcardRepository.save(Giftcard);
            }
        }
    }

    private void validateUserLink(Giftcard Giftcard, CustomUser user) {
        if (Giftcard.getLinkedUser() == null) {
            throw new IllegalStateException("Gift card must be linked to a user before use");
        }

        if (!Giftcard.getLinkedUser().getUserId().equals(user.getUserId())) {
            Giftcard.setFailedAttempts(Giftcard.getFailedAttempts() + 1);
            Giftcard.setLastFailedAttempt(LocalDateTime.now());
            GiftcardRepository.save(Giftcard);
            throw new IllegalStateException("Gift card can only be used by the linked user");
        }
    }
}