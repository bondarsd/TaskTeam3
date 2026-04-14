package com.team3.device.web.controller;

import com.team3.device.application.service.ProductApplicationService;
import com.team3.device.web.dto.ProductResponse;
import com.team3.device.web.exception.DuplicateResourceException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductController.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private ProductApplicationService productApplicationService;

    @Test
    void createProduct_shouldReturnProduct() throws Exception {

        ProductResponse response = new ProductResponse(
                1L,
                "Router",
                "TP-Link",
                BigDecimal.valueOf(100),
                "WiFi-6",
                1200,
                "5Ghz",
                4,
                1
        );

        when(productApplicationService.createProduct(any()))
                .thenReturn(response);

        String requestJson = """
                {
                    "name": "Router",
                    "brand": "TP-Link",
                    "price": 100
                }
                """;

        mockMvc.perform(post("/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Router"));
    }

    @Test
    void getAllProducts_shouldReturnList() throws Exception {

        ProductResponse response = new ProductResponse(
                1L,
                "Router",
                "TP-Link",
                BigDecimal.valueOf(100),
                null,
                null,
                null,
                null,
                null
        );

        when(productApplicationService.getAllProducts()).thenReturn(List.of(response));

        mockMvc.perform(get("/products"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].name").value("Router"));
    }

    @Test
    void createProduct_shouldReturnConflict_whenDuplicateName() throws Exception {

        when(productApplicationService.createProduct(any()))
                .thenThrow(new DuplicateResourceException("Product with this name already exists"));

        String requestJson = """
                {
                    "name": "Router",
                    "brand": "TP-Link",
                    "price": 100
                }
                """;

        mockMvc.perform(post("/products")
                .contentType(MediaType.APPLICATION_JSON)
                .content(requestJson))
                .andExpect(status().isConflict());
    }
}
