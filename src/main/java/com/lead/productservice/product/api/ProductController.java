package com.lead.productservice.product.api;

import com.lead.productservice.product.application.dto.CreateProductRequest;
import com.lead.productservice.product.application.dto.CreateComplexProductRequest;
import com.lead.productservice.product.application.dto.ProductDetailedResponse;
import com.lead.productservice.product.application.dto.ProductResponse;
import com.lead.productservice.product.application.service.ProductService;
import com.lead.productservice.shared.api.PageResponse;
import jakarta.validation.Valid;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Min;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@RequestMapping("/api/products")
public class ProductController {

    private final ProductService productService;

    public ProductController(ProductService productService) {
        this.productService = productService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public ProductResponse create(@Valid @RequestBody CreateProductRequest request) {
        return productService.create(request);
    }

    @PostMapping("/complex")
    @ResponseStatus(HttpStatus.CREATED)
    public ProductResponse createComplex(@Valid @RequestBody CreateComplexProductRequest request) {
        return productService.createComplex(request);
    }

    @GetMapping("/{id}")
    public ProductResponse findById(@PathVariable Long id) {
        return productService.findById(id);
    }

    @GetMapping("/{id}/detailed")
    public ProductDetailedResponse findDetailedById(
            @PathVariable Long id,
            @RequestParam(required = false) String source,
            @RequestParam(required = false) String currency
    ) {
        return productService.findDetailedById(id, source, currency);
    }

    @GetMapping
    public List<ProductResponse> findAll() {
        return productService.findAll();
    }

    @GetMapping("/search")
    public List<ProductResponse> searchByName(@RequestParam String name) {
        return productService.searchByName(name);
    }

    @GetMapping("/price-range")
    public PageResponse<ProductResponse> findByPriceRange(
            @RequestParam @DecimalMin(value = "0.00") BigDecimal minPrice,
            @RequestParam @DecimalMin(value = "0.01") BigDecimal maxPrice,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(1) int size
    ) {
        return productService.findByPriceRange(minPrice, maxPrice, page, size);
    }

    @GetMapping("/search/advanced")
    public PageResponse<ProductResponse> searchAdvanced(
            @RequestParam String name,
            @RequestParam(defaultValue = "0.00") @DecimalMin(value = "0.00") BigDecimal minPrice,
            @RequestParam(defaultValue = "0") @Min(0) int page,
            @RequestParam(defaultValue = "10") @Min(1) int size
    ) {
        return productService.searchByNameAndMinPrice(name, minPrice, page, size);
    }
}
