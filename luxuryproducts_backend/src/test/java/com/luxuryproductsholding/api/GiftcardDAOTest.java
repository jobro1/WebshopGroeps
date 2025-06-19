package com.luxuryproductsholding.api;

import com.luxuryproductsholding.api.dao.GiftcardDAO;
import com.luxuryproductsholding.api.dao.GiftcardRepository;
import com.luxuryproductsholding.api.dao.UserDAO;
import com.luxuryproductsholding.api.dto.CreateGiftcardRequest;
import com.luxuryproductsholding.api.models.CustomUser;
import com.luxuryproductsholding.api.models.Giftcard;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GiftcardDAOTest {

    @Mock
    private GiftcardRepository giftcardRepository;

    @Mock
    private UserDAO userDAO;

    @InjectMocks
    private GiftcardDAO giftcardDAO;

    private CustomUser testUser;
    private Giftcard testGiftcard;
    private CreateGiftcardRequest validRequest;

    @BeforeEach
    void setUp() {
        // Set up test user
        testUser = new CustomUser();
        testUser.setUserId(1L);
        testUser.setEmail("test@example.com");

        // Set up test gift card
        testGiftcard = new Giftcard();
        testGiftcard.setCode("LPHTEST123456");
        testGiftcard.setInitialValue(new BigDecimal("100.00"));
        testGiftcard.setCurrentBalance(new BigDecimal("100.00"));
        testGiftcard.setStatus(Giftcard.GiftcardStatus.ACTIVE);
        testGiftcard.setCreatedAt(LocalDateTime.now());
        testGiftcard.setExpirationDate(LocalDateTime.now().plusYears(1));

        // Set up valid request
        validRequest = new CreateGiftcardRequest();
        validRequest.setValue(new BigDecimal("100.00"));
        validRequest.setRecipientEmail("recipient@example.com");
        validRequest.setMessage("Test message");
    }

    @Test
    void createGiftcard_WithValidRequest_ShouldCreateGiftcard() {
        // Arrange
        when(userDAO.getUserByEmail(anyString())).thenReturn(testUser);
        when(giftcardRepository.save(any(Giftcard.class))).thenReturn(testGiftcard);

        // Act
        Giftcard result = giftcardDAO.createGiftcard(validRequest);

        // Assert
        assertNotNull(result);
        assertEquals(testGiftcard.getInitialValue(), result.getInitialValue());
        assertEquals(testGiftcard.getCurrentBalance(), result.getCurrentBalance());
        assertEquals(Giftcard.GiftcardStatus.ACTIVE, result.getStatus());
        verify(giftcardRepository).save(any(Giftcard.class));
    }

    @Test
    void createGiftcard_WithInvalidAmount_ShouldThrowException() {
        // Arrange
        validRequest.setValue(new BigDecimal("123.45")); // Invalid amount

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () -> giftcardDAO.createGiftcard(validRequest));
        verify(giftcardRepository, never()).save(any(Giftcard.class));
    }

    @Test
    void linkToUser_WithValidCodeAndUser_ShouldLinkGiftcard() {
        // Arrange
        when(giftcardRepository.findByCode(anyString())).thenReturn(Optional.of(testGiftcard));
        when(giftcardRepository.save(any(Giftcard.class))).thenReturn(testGiftcard);

        // Act
        Giftcard result = giftcardDAO.linkToUser("LPHTEST123456", testUser);

        // Assert
        assertNotNull(result);
        assertEquals(testUser, result.getLinkedUser());
        assertNotNull(result.getActivatedAt());
        verify(giftcardRepository).save(any(Giftcard.class));
    }

    @Test
    void linkToUser_WithNonExistentCode_ShouldThrowException() {
        // Arrange
        when(giftcardRepository.findByCode(anyString())).thenReturn(Optional.empty());

        // Act & Assert
        ResponseStatusException exception = assertThrows(ResponseStatusException.class,
                () -> giftcardDAO.linkToUser("NONEXISTENT", testUser));
        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    }

    @Test
    void useGiftcard_WithSufficientBalance_ShouldUpdateBalance() {
        // Arrange
        testGiftcard.setLinkedUser(testUser);
        when(giftcardRepository.findByCode(anyString())).thenReturn(Optional.of(testGiftcard));
        BigDecimal useAmount = new BigDecimal("50.00");

        // Act
        giftcardDAO.useGiftcard("LPHTEST123456", useAmount, testUser);

        // Assert
        assertEquals(new BigDecimal("50.00"), testGiftcard.getCurrentBalance());
        verify(giftcardRepository).save(testGiftcard);
    }

    @Test
    void useGiftcard_WithInsufficientBalance_ShouldThrowException() {
        // Arrange
        testGiftcard.setLinkedUser(testUser);
        when(giftcardRepository.findByCode(anyString())).thenReturn(Optional.of(testGiftcard));
        BigDecimal useAmount = new BigDecimal("150.00"); // More than balance

        // Act & Assert
        assertThrows(IllegalStateException.class,
                () -> giftcardDAO.useGiftcard("LPHTEST123456", useAmount, testUser));
    }

    @Test
    void updateBalance_WithValidNewBalance_ShouldUpdateBalance() {
        // Arrange
        testGiftcard.setLinkedUser(testUser);
        when(giftcardRepository.findByCode(anyString())).thenReturn(Optional.of(testGiftcard));
        when(giftcardRepository.save(any(Giftcard.class))).thenAnswer(invocation -> invocation.getArgument(0));
        BigDecimal newBalance = new BigDecimal("75.00");

        // Act
        Giftcard result = giftcardDAO.updateBalance("LPHTEST123456", newBalance, testUser);

        // Assert
        assertEquals(newBalance, result.getCurrentBalance());
        assertEquals(Giftcard.GiftcardStatus.ACTIVE, result.getStatus());
        verify(giftcardRepository).save(any(Giftcard.class));
    }

    @Test
    void updateBalance_ToZero_ShouldMarkAsUsed() {
        // Arrange
        testGiftcard.setLinkedUser(testUser);
        when(giftcardRepository.findByCode(anyString())).thenReturn(Optional.of(testGiftcard));
        when(giftcardRepository.save(any(Giftcard.class))).thenAnswer(invocation -> invocation.getArgument(0));
        BigDecimal newBalance = BigDecimal.ZERO;

        // Act
        Giftcard result = giftcardDAO.updateBalance("LPHTEST123456", newBalance, testUser);

        // Assert
        assertEquals(newBalance, result.getCurrentBalance());
        assertEquals(Giftcard.GiftcardStatus.USED, result.getStatus());
        verify(giftcardRepository).save(any(Giftcard.class));
    }

    @Test
    void updateBalance_WithNegativeAmount_ShouldThrowException() {
        // Arrange
        testGiftcard.setLinkedUser(testUser);
        when(giftcardRepository.findByCode(anyString())).thenReturn(Optional.of(testGiftcard));
        BigDecimal newBalance = new BigDecimal("-50.00");

        // Act & Assert
        assertThrows(IllegalArgumentException.class,
                () -> giftcardDAO.updateBalance("LPHTEST123456", newBalance, testUser));
    }

    @Test
    void getGiftcardsForUser_ShouldReturnUserGiftcards() {
        // Arrange
        when(giftcardRepository.findByLinkedUser(testUser))
                .thenReturn(java.util.List.of(testGiftcard));

        // Act
        var result = giftcardDAO.getGiftcardsForUser(testUser);

        // Assert
        assertFalse(result.isEmpty());
        assertEquals(1, result.size());
        assertEquals(testGiftcard, result.get(0));
    }
}