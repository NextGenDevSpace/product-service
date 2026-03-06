package com.lead.productservice.product.application.dto;

import java.math.BigDecimal;
import java.time.Instant;

public record ProductDetailedResponse(
        ProductData product,
        PriceData pricing,
        AuditData audit
) {

    public record ProductData(Long id, String name) {
    }

    public record PriceData(BigDecimal amount, String currency) {
    }

    public record AuditData(String source, Instant mappedAt) {
    }
}
