package com.luxuryproductsholding.api.controller;

import com.luxuryproductsholding.api.dao.GiftcardDAO;
import com.luxuryproductsholding.api.dto.CreateGiftcardRequest;
import com.luxuryproductsholding.api.dto.GiftcardDTO;
import com.luxuryproductsholding.api.models.Giftcard;
import com.luxuryproductsholding.api.models.CustomUser;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.Authentication;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/giftcards")
@CrossOrigin(origins = "http://localhost:4200")
public class GiftcardController {
    private final GiftcardDAO GiftcardDAO;

    public GiftcardController(GiftcardDAO GiftcardDAO) {
        this.GiftcardDAO = GiftcardDAO;
    }

    @PostMapping
    public ResponseEntity<GiftcardDTO> createGiftcard(@RequestBody CreateGiftcardRequest request) {
        Giftcard Giftcard = GiftcardDAO.createGiftcard(request);
        return ResponseEntity.ok(convertToDTO(Giftcard));
    }

    @PostMapping("/{code}/link")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<GiftcardDTO> linkGiftcard(@PathVariable String code) {
        Giftcard Giftcard = GiftcardDAO.linkToUser(code, getCurrentUser());
        return ResponseEntity.ok(convertToDTO(Giftcard));
    }

    @PatchMapping("/{code}/balance")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<GiftcardDTO> updateBalance(
            @PathVariable String code,
            @RequestBody UpdateBalanceRequest request) {
        Giftcard giftcard = GiftcardDAO.updateBalance(code, new BigDecimal(request.balance), getCurrentUser());
        return ResponseEntity.ok(convertToDTO(giftcard));
    }

    @GetMapping("/valid-amounts")
    public ResponseEntity<List<BigDecimal>> getValidAmounts() {
        return ResponseEntity.ok(List.of(
                new BigDecimal("50.00"),
                new BigDecimal("100.00"),
                new BigDecimal("200.00"),
                new BigDecimal("400.00"),
                new BigDecimal("780.00")
        ));
    }

    @GetMapping("/my-cards")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<GiftcardDTO>> getMyGiftcards() {
        List<Giftcard> Giftcards = GiftcardDAO.getGiftcardsForUser(getCurrentUser());
        return ResponseEntity.ok(Giftcards.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList()));
    }

    @GetMapping("/admin")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<GiftcardDTO>> getAllGiftcards(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String email,
            @RequestParam(required = false) Boolean expired) {
        List<Giftcard> Giftcards = GiftcardDAO.searchGiftcards(status, email, expired);
        return ResponseEntity.ok(Giftcards.stream()
                .map(this::convertToDTO)
                .collect(Collectors.toList()));
    }

    private GiftcardDTO convertToDTO(Giftcard Giftcard) {
        GiftcardDTO dto = new GiftcardDTO(
                Giftcard.getCode(),
                Giftcard.getInitialValue(),
                Giftcard.getCurrentBalance(),
                Giftcard.getExpirationDate(),
                Giftcard.getCreatedAt(),
                Giftcard.getActivatedAt(),
                Giftcard.getMessage(),
                Giftcard.getRecipientEmail(),
                Giftcard.getLinkedUser() != null ? Giftcard.getLinkedUser().getEmail() : null,
                Giftcard.getStatus()
        );
        return dto;
    }

    private CustomUser getCurrentUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new IllegalStateException("No authenticated user found");
        }

        Object principal = authentication.getPrincipal();
        if (principal instanceof CustomUser) {
            return (CustomUser) principal;
        }

        throw new IllegalStateException("Authenticated user is not of type CustomUser");
    }

    private static class UpdateBalanceRequest {
        private double balance;

        public double getBalance() {
            return balance;
        }

        public void setBalance(double balance) {
            this.balance = balance;
        }
    }
}