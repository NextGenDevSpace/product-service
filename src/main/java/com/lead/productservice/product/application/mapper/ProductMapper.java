package com.lead.productservice.product.application.mapper;

import com.lead.productservice.product.application.dto.CreateProductRequest;
import com.lead.productservice.product.application.dto.CreateComplexProductRequest;
import com.lead.productservice.product.application.dto.ProductDetailedResponse;
import com.lead.productservice.product.application.dto.ProductResponse;
import com.lead.productservice.product.domain.entity.ProductEntity;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    ProductEntity toEntity(CreateProductRequest request);

    @Mapping(source = "product.name", target = "name", qualifiedByName = "trimName")
    @Mapping(source = "pricing.amount", target = "price")
    ProductEntity toEntity(CreateComplexProductRequest request);

    ProductResponse toResponse(ProductEntity entity);

    @Mapping(target = "product", expression = "java(new ProductDetailedResponse.ProductData(entity.getId(), entity.getName()))")
    @Mapping(target = "pricing", expression = "java(new ProductDetailedResponse.PriceData(entity.getPrice(), currency == null || currency.isBlank() ? \"USD\" : currency.trim().toUpperCase()))")
    @Mapping(target = "audit", expression = "java(new ProductDetailedResponse.AuditData(source == null || source.isBlank() ? \"api\" : source.trim(), java.time.Instant.now()))")
    ProductDetailedResponse toDetailedResponse(ProductEntity entity, String source, String currency);

    @Named("trimName")
    default String trimName(String value) {
        return value == null ? null : value.trim();
    }
}
