package com.lead.productservice.product.application.mapper;

import com.lead.productservice.product.application.dto.CreateProductRequest;
import com.lead.productservice.product.application.dto.ProductResponse;
import com.lead.productservice.product.domain.entity.ProductEntity;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface ProductMapper {

    ProductEntity toEntity(CreateProductRequest request);

    ProductResponse toResponse(ProductEntity entity);
}
