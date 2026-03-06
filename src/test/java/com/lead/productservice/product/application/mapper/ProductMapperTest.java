package com.lead.productservice.product.application.mapper;

import com.lead.productservice.product.application.dto.CreateComplexProductRequest;
import com.lead.productservice.product.application.dto.CreateProductRequest;
import com.lead.productservice.product.application.dto.ProductDetailedResponse;
import com.lead.productservice.product.application.dto.ProductResponse;
import com.lead.productservice.product.domain.entity.ProductEntity;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import org.mapstruct.factory.Mappers;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

class ProductMapperTest {

    private final ProductMapper mapper = Mappers.getMapper(ProductMapper.class);

    @Test
    void toEntity_mapsSimpleRequest() {
        CreateProductRequest request = new CreateProductRequest("Keyboard", new BigDecimal("1499.99"));

        ProductEntity entity = mapper.toEntity(request);

        assertNotNull(entity);
        assertEquals("Keyboard", entity.getName());
        assertEquals(new BigDecimal("1499.99"), entity.getPrice());
    }

    @Test
    void toEntity_mapsComplexNestedRequest() {
        CreateComplexProductRequest request = new CreateComplexProductRequest(
                new CreateComplexProductRequest.ProductData("  Mouse Pro  "),
                new CreateComplexProductRequest.PriceData(new BigDecimal("899.90"), "mxn"),
                new CreateComplexProductRequest.AuditData("mobile")
        );

        ProductEntity entity = mapper.toEntity(request);

        assertNotNull(entity);
        assertEquals("Mouse Pro", entity.getName());
        assertEquals(new BigDecimal("899.90"), entity.getPrice());
    }

    @Test
    void toDetailedResponse_mapsNestedWithProvidedSourceAndCurrency() {
        ProductEntity entity = new ProductEntity("Monitor", new BigDecimal("4999.00"));
        setEntityId(entity, 10L);

        ProductDetailedResponse response = mapper.toDetailedResponse(entity, "web", "mxn");

        assertNotNull(response);
        assertEquals(10L, response.product().id());
        assertEquals("Monitor", response.product().name());
        assertEquals(new BigDecimal("4999.00"), response.pricing().amount());
        assertEquals("MXN", response.pricing().currency());
        assertEquals("web", response.audit().source());
        assertNotNull(response.audit().mappedAt());
    }

    @Test
    void toDetailedResponse_usesDefaultsWhenSourceAndCurrencyAreMissing() {
        ProductEntity entity = new ProductEntity("Headphones", new BigDecimal("1299.00"));
        setEntityId(entity, 22L);

        ProductDetailedResponse response = mapper.toDetailedResponse(entity, "   ", null);

        assertNotNull(response);
        assertEquals(22L, response.product().id());
        assertEquals("USD", response.pricing().currency());
        assertEquals("api", response.audit().source());
        assertNotNull(response.audit().mappedAt());
    }

    private static void setEntityId(ProductEntity entity, Long id) {
        try {
            Field idField = ProductEntity.class.getDeclaredField("id");
            idField.setAccessible(true);
            idField.set(entity, id);
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("Unable to set ProductEntity id for test", e);
        }
    }
}
