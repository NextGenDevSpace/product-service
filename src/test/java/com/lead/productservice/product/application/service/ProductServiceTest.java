package com.lead.productservice.product.application.service;

import com.lead.productservice.product.application.dto.CreateComplexProductRequest;
import com.lead.productservice.product.application.dto.ProductDetailedResponse;
import com.lead.productservice.product.application.dto.ProductResponse;
import com.lead.productservice.product.application.mapper.ProductMapper;
import com.lead.productservice.product.domain.entity.ProductEntity;
import com.lead.productservice.product.domain.exception.ProductNotFoundException;
import com.lead.productservice.product.domain.repository.ProductRepository;
import java.lang.reflect.Field;
import java.math.BigDecimal;
import java.util.Optional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mapstruct.factory.Mappers;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ProductServiceTest {

    @Mock
    private ProductRepository productRepository;

    private ProductService productService;

    @BeforeEach
    void setUp() {
        ProductMapper mapper = Mappers.getMapper(ProductMapper.class);
        productService = new ProductService(productRepository, mapper);
    }

    @Test
    void createComplex_mapsNestedRequestAndReturnsResponse() {
        CreateComplexProductRequest request = new CreateComplexProductRequest(
                new CreateComplexProductRequest.ProductData("  Mechanical Keyboard  "),
                new CreateComplexProductRequest.PriceData(new BigDecimal("1999.50"), "mxn"),
                new CreateComplexProductRequest.AuditData("web")
        );

        when(productRepository.save(any(ProductEntity.class))).thenAnswer(invocation -> {
            ProductEntity entity = invocation.getArgument(0);
            setEntityId(entity, 101L);
            return entity;
        });

        ProductResponse response = productService.createComplex(request);

        ArgumentCaptor<ProductEntity> entityCaptor = ArgumentCaptor.forClass(ProductEntity.class);
        verify(productRepository).save(entityCaptor.capture());

        ProductEntity persisted = entityCaptor.getValue();
        assertEquals("Mechanical Keyboard", persisted.getName());
        assertEquals(new BigDecimal("1999.50"), persisted.getPrice());

        assertNotNull(response);
        assertEquals(101L, response.id());
        assertEquals("Mechanical Keyboard", response.name());
        assertEquals(new BigDecimal("1999.50"), response.price());
    }

    @Test
    void findDetailedById_appliesDefaultsWhenSourceAndCurrencyAreMissing() {
        ProductEntity entity = new ProductEntity("Mouse", new BigDecimal("799.00"));
        setEntityId(entity, 7L);
        when(productRepository.findById(7L)).thenReturn(Optional.of(entity));

        ProductDetailedResponse response = productService.findDetailedById(7L, "   ", null);

        assertNotNull(response);
        assertEquals(7L, response.product().id());
        assertEquals("Mouse", response.product().name());
        assertEquals(new BigDecimal("799.00"), response.pricing().amount());
        assertEquals("USD", response.pricing().currency());
        assertEquals("api", response.audit().source());
        assertNotNull(response.audit().mappedAt());
    }

    @Test
    void findDetailedById_throwsWhenProductDoesNotExist() {
        when(productRepository.findById(999L)).thenReturn(Optional.empty());

        ProductNotFoundException ex = assertThrows(
                ProductNotFoundException.class,
                () -> productService.findDetailedById(999L, "web", "mxn")
        );

        assertEquals("Product with id 999 was not found", ex.getMessage());
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
