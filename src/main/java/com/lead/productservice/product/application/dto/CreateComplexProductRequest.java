package com.lead.productservice.product.application.dto;

import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import java.math.BigDecimal;

public record CreateComplexProductRequest(
        @NotNull @Valid ProductData product,
        @NotNull @Valid PriceData pricing,
        @Valid AuditData audit
) {

    public record ProductData(
            @NotBlank(message = "name is required")
            @Size(max = 100, message = "name max length is 100")
            String name
    ) {
    }

    public record PriceData(
            @NotNull(message = "amount is required")
            @DecimalMin(value = "0.01", message = "price must be greater than 0")
            BigDecimal amount,
            String currency
    ) {
    }

    public record AuditData(String source) {
    }
}
