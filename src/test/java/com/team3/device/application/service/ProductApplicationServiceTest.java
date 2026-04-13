package com.team3.device.application.service;

import com.team3.device.domain.model.Product;
import com.team3.device.domain.repository.ProductRepository;
import com.team3.device.web.dto.CreateProductRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductApplicationServiceTest {

    @Mock
    private ProductRepository productRepository;

    @InjectMocks
    private ProductApplicationService productApplicationService;

    @Test
    void createProduct_shouldReturnResponse() {

        // given
        CreateProductRequest request = new CreateProductRequest();
        request.setName("Router");
        request.setBrand("TP-Link");
        request.setPrice(BigDecimal.valueOf(100));

        Product savedProduct = new Product();
        savedProduct.setId(1L);
        savedProduct.setName("Router");
        savedProduct.setBrand("TP-Link");
        savedProduct.setPrice(BigDecimal.valueOf(100));

        when(productRepository.save(any(Product.class))).thenReturn(savedProduct);

        // when
        var result = productApplicationService.createProduct(request);

        // then
        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals("Router", result.getName());

        verify(productRepository, times(1)).save(any(Product.class));
    }

    @Test
    void getAllProducts_shouldReturnList() {

        // given
        Product product = new Product();
        product.setId(1L);
        product.setName("Router");

        when(productRepository.findAll()).thenReturn(List.of(product));

        // when
        var result = productApplicationService.getAllProducts();

        // then
        assertEquals(1, result.size());
        assertEquals(1L, result.get(0).getId());
        assertEquals("Router", result.get(0).getName());

        verify(productRepository, times(1)).findAll();
    }
}
