package com.lead.productservice.product.application.service;

import com.lead.productservice.product.application.dto.CreateProductRequest;
import com.lead.productservice.product.application.dto.ProductResponse;
import com.lead.productservice.product.application.mapper.ProductMapper;
import com.lead.productservice.product.domain.entity.ProductEntity;
import com.lead.productservice.product.domain.exception.ProductNotFoundException;
import com.lead.productservice.product.domain.repository.ProductRepository;
import com.lead.productservice.shared.api.PageResponse;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ProductService {

    private final ProductRepository productRepository;
    private final ProductMapper productMapper;

    public ProductService(ProductRepository productRepository, ProductMapper productMapper) {
        this.productRepository = productRepository;
        this.productMapper = productMapper;
    }

    @Transactional
    public ProductResponse create(CreateProductRequest request) {
        ProductEntity entity = productMapper.toEntity(request);
        ProductEntity saved = productRepository.save(entity);
        return productMapper.toResponse(saved);
    }

    @Transactional(readOnly = true)
    public ProductResponse findById(Long id) {
        ProductEntity entity = productRepository.findById(id)
                .orElseThrow(() -> new ProductNotFoundException(id));
        return productMapper.toResponse(entity);
    }

    @Transactional(readOnly = true)
    public List<ProductResponse> findAll() {
        return productRepository.findAll()
                .stream()
                .map(productMapper::toResponse)
                .toList();
    }

    @Transactional(readOnly = true)
    public List<ProductResponse> searchByName(String name) {
        return productRepository.findByNameContainingIgnoreCase(name)
                .stream()
                .map(productMapper::toResponse)
                .toList();
    }

        @Transactional(readOnly = true)
        public PageResponse<ProductResponse> findByPriceRange(BigDecimal minPrice, BigDecimal maxPrice, int page, int size) {
        PageRequest pageable = PageRequest.of(page, size);
        Page<ProductResponse> result = productRepository
            .findByPriceBetweenOrderByPriceAsc(minPrice, maxPrice, pageable)
            .map(productMapper::toResponse);

        return new PageResponse<>(
            result.getContent(),
            result.getNumber(),
            result.getSize(),
            result.getTotalElements(),
            result.getTotalPages(),
            result.isLast()
        );
        }

        @Transactional(readOnly = true)
        public PageResponse<ProductResponse> searchByNameAndMinPrice(String name, BigDecimal minPrice, int page, int size) {
        PageRequest pageable = PageRequest.of(page, size);
        Page<ProductResponse> result = productRepository
            .searchByNameAndMinPrice(name, minPrice, pageable)
            .map(productMapper::toResponse);

        return new PageResponse<>(
            result.getContent(),
            result.getNumber(),
            result.getSize(),
            result.getTotalElements(),
            result.getTotalPages(),
            result.isLast()
        );
        }
}
