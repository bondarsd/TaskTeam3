package com.team3.device.application.service;

import com.team3.device.domain.model.Product;
import com.team3.device.domain.repository.ProductRepository;
import com.team3.device.web.dto.CreateProductRequest;
import com.team3.device.web.dto.ProductResponse;
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

        Product product = new Product();

        product.setCustomerId(request.getCustomerId());
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

    public List<ProductResponse> getProductsByCustomerId(Long customerId) {

        return productRepository.findByCustomerId(customerId)
                .stream()
                .map(this::mapToResponse)
                .collect(Collectors.toList());
    }

    private ProductResponse mapToResponse(Product product) {
        return new ProductResponse(
                product.getId(),
                product.getCustomerId(),
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
}
