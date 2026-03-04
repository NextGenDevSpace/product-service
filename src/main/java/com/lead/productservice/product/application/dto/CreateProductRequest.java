package com.lead.productservice.product.application.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public record CreateProductRequest(
        @NotBlank(message = "name is required")
        @Size(max = 100, message = "name max length is 100")
        String name,
        @DecimalMin(value = "0.01", message = "price must be greater than 0")
        BigDecimal price
) {
}
