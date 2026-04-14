package com.team3.device.web.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

import java.math.BigDecimal;

@Getter
@AllArgsConstructor
public class ProductResponse {

    private Long id;

    private String name;

    private String brand;

    private BigDecimal price;

    private String wifiStandard;

    private Integer maxSpeedMbps;

    private String frequencyBand;

    private Integer lanPorts;

    private Integer wanPorts;
}
