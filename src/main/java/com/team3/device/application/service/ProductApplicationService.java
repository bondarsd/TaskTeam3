package com.team3.device.application.service;

import com.team3.device.domain.model.Product;
import com.team3.device.domain.repository.ProductRepository;
import com.team3.device.web.dto.CreateProductRequest;
import com.team3.device.web.dto.ProductResponse;
import com.team3.device.web.exception.DuplicateResourceException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class ProductApplicationService {

    private final ProductRepository productRepository;

    public ProductApplicationService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public ProductResponse createProduct(CreateProductRequest request) {

        if (productRepository.existsByName(request.getName())) {
            throw new DuplicateResourceException("Product with this name already exists");
        }

        Product product = new Product();

        product.setName(request.getName());
        product.setBrand(request.getBrand());
        product.setPrice(request.getPrice());
        product.setWifiStandard(request.getWifiStandard());
        product.setMaxSpeedMbps(request.getMaxSpeedMbps());
        product.setFrequencyBand(request.getFrequencyBand());
        product.setLanPorts(request.getLanPorts());
        product.setWanPorts(request.getWanPorts());

        Product saved = productRepository.save(product);
        return mapToResponse(saved);
    }

    private ProductResponse mapToResponse(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getName(),
                product.getBrand(),
                product.getPrice(),
                product.getWifiStandard(),
                product.getMaxSpeedMbps(),
                product.getFrequencyBand(),
                product.getLanPorts(),
                product.getWanPorts()
        );
    }

    public List<ProductResponse> getAllProducts() {
        return productRepository.findAll()
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }
}
