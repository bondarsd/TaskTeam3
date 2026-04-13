package com.team3.device.web.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class CreateCustomerRequest {

    @NotBlank(message = "Name must not be blank")
    @Pattern(
            regexp = "^[a-zA-Z ]+$",
            message = "Name must contain only letters and spaces"
    )
    private String name;

    @NotBlank(message = "Email must not be blank")
    @Email(message = "Email must be valid")
    private String email;
}
