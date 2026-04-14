package com.team3.device.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
public class CreateProductRequest {

    @NotBlank
    private String name;

    @NotBlank
    private String brand;

    @NotNull
    @Positive
    private BigDecimal price;

    private String wifiStandard;

    private Integer maxSpeedMbps;

    private String frequencyBand;

    private Integer lanPorts;

    private Integer wanPorts;
}
