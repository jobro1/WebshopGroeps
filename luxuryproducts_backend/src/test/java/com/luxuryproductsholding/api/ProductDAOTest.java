package com.luxuryproductsholding.api;

import com.luxuryproductsholding.api.dto.ProductVariationCreateDTO;
import com.luxuryproductsholding.api.dto.ProductVariationUpdateDTO;
import com.luxuryproductsholding.api.models.*;
import com.luxuryproductsholding.api.dao.*;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class ProductDAOTest {

    @Mock
    private ProductRepository productRepository;
    @Mock
    private ProductVariationRepository productVariationRepository;
    @Mock
    private VariationValueRepository variationValueRepository;
    @Mock
    private VariationRepository variationRepository;

    @InjectMocks
    private ProductDAO productDAO;

    @BeforeEach
    public void setup() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    public void testCreateProductVariation_withNewVariationValue() {
        ProductVariationCreateDTO dto = new ProductVariationCreateDTO();
        dto.sku = "SKU001";
        dto.price = 100.0;
        dto.stock = 10;
        dto.imageUrl = "img.png";
        dto.productId = 1L;

        ProductVariationCreateDTO.VariationValueDTO valueDTO = new ProductVariationCreateDTO.VariationValueDTO();
        valueDTO.variationName = "Color";
        valueDTO.value = "Red";
        dto.values = List.of(valueDTO);

        Product product = new Product();
        product.setProduct_id(1L);

        Variation variation = new Variation();
        variation.setName("Color");

        VariationValue variationValue = new VariationValue();
        variationValue.setValue("Red");
        variationValue.setVariation(variation);

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(variationRepository.findByVariationName("Color")).thenReturn(Optional.empty());
        when(variationRepository.save(any())).thenReturn(variation);
        when(variationValueRepository.findByValueAndVariation_VariationName("Color", "Red"))
                .thenReturn(Optional.empty());
        when(variationValueRepository.save(any())).thenReturn(variationValue);
        when(productVariationRepository.save(any())).thenAnswer(i -> i.getArguments()[0]);

        ProductVariation result = productDAO.createProductVariation(dto);

        assertEquals("SKU001", result.getSku());
        assertEquals(1, result.getValues().size());
    }

    @Test
    public void testUpdateProductVariation_success() {
        ProductVariationUpdateDTO dto = new ProductVariationUpdateDTO();
        dto.productVariationId = 1L;
        dto.sku = "SKU002";
        dto.price = 120.0;
        dto.stock = 20;
        dto.imageUrl = "new.png";

        ProductVariationUpdateDTO.VariationValueDTO valDto = new ProductVariationUpdateDTO.VariationValueDTO();
        valDto.setVariationValueId(2L);
        valDto.setValue("Blue");
        dto.values = List.of(valDto);

        ProductVariation pv = new ProductVariation();
        pv.setSku("OLD");
        pv.setValues(new ArrayList<>());

        VariationValue vv = new VariationValue();
        vv.setValue("Red");

        when(productVariationRepository.findById(1L)).thenReturn(Optional.of(pv));
        when(variationValueRepository.findById(2L)).thenReturn(Optional.of(vv));
        when(productVariationRepository.save(any())).thenAnswer(i -> i.getArguments()[0]);

        ProductVariation updated = productDAO.updateProductVariation(dto);

        assertEquals("SKU002", updated.getSku());
        assertEquals(120.0, updated.getPrice());
        assertEquals(1, updated.getValues().size());
        assertEquals("Blue", updated.getValues().get(0).getValue());
    }

    @Test
    public void testDeleteProductVariation_success() {
        ProductVariation pv = new ProductVariation();
        pv.setValues(new ArrayList<>(List.of(new VariationValue())));

        when(productVariationRepository.findById(1L)).thenReturn(Optional.of(pv));

        productDAO.deleteProductVariation(1L);

        verify(productVariationRepository).delete(pv);
        assertTrue(pv.getValues().isEmpty());
    }

    @Test
    public void testDeleteProductVariation_notFound() {
        when(productVariationRepository.findById(999L)).thenReturn(Optional.empty());

        ResponseStatusException exception = assertThrows(ResponseStatusException.class, () ->
                productDAO.deleteProductVariation(999L));

        assertEquals(HttpStatus.NOT_FOUND, exception.getStatusCode());
    }
}