package com.team3.device.web.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CustomerResponse {

    private Long id;
    private String name;
    private String email;
}
