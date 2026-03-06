package com.lead.productservice.product.api;

import com.lead.productservice.product.application.dto.ProductDetailedResponse;
import com.lead.productservice.product.application.dto.ProductResponse;
import com.lead.productservice.product.application.service.ProductService;
import com.lead.productservice.product.domain.exception.ProductNotFoundException;
import com.lead.productservice.shared.api.GlobalExceptionHandler;
import com.lead.productservice.shared.api.PageResponse;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(ProductController.class)
@AutoConfigureMockMvc(addFilters = false)
@Import(GlobalExceptionHandler.class)
class ProductControllerTest {

    @Autowired
    private MockMvc mockMvc;

        @MockitoBean
    private ProductService productService;

    @Test
    void create_returnsCreatedAndResponseBody() throws Exception {
        ProductResponse response = new ProductResponse(1L, "Keyboard", new BigDecimal("1499.99"));
        when(productService.create(org.mockito.ArgumentMatchers.any())).thenReturn(response);

        String requestBody = """
                {
                  "name": "Keyboard",
                  "price": 1499.99
                }
                """;

        mockMvc.perform(post("/api/products")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(1))
                .andExpect(jsonPath("$.name").value("Keyboard"))
                .andExpect(jsonPath("$.price").value(1499.99));
    }

    @Test
    void createComplex_whenNestedNameIsBlank_returnsBadRequest() throws Exception {
        String requestBody = """
                {
                  "product": { "name": "   " },
                  "pricing": { "amount": 100.00, "currency": "mxn" },
                  "audit": { "source": "web" }
                }
                """;

        mockMvc.perform(post("/api/products/complex")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(requestBody))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.title").value("Validation failed"))
                .andExpect(jsonPath("$.errors['product.name']").value("name is required"));
    }

    @Test
    void findDetailedById_returnsDetailedPayload() throws Exception {
        ProductDetailedResponse response = new ProductDetailedResponse(
                new ProductDetailedResponse.ProductData(7L, "Mouse"),
                new ProductDetailedResponse.PriceData(new BigDecimal("799.00"), "MXN"),
                new ProductDetailedResponse.AuditData("web", Instant.parse("2026-03-05T18:00:00Z"))
        );

        when(productService.findDetailedById(eq(7L), eq("web"), eq("mxn"))).thenReturn(response);

        mockMvc.perform(get("/api/products/7/detailed")
                        .param("source", "web")
                        .param("currency", "mxn"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.product.id").value(7))
                .andExpect(jsonPath("$.product.name").value("Mouse"))
                .andExpect(jsonPath("$.pricing.amount").value(799.00))
                .andExpect(jsonPath("$.pricing.currency").value("MXN"))
                .andExpect(jsonPath("$.audit.source").value("web"))
                .andExpect(jsonPath("$.audit.mappedAt").value("2026-03-05T18:00:00Z"));
    }

    @Test
    void findById_whenNotFound_returnsProblemDetail() throws Exception {
        when(productService.findById(999L)).thenThrow(new ProductNotFoundException(999L));

        mockMvc.perform(get("/api/products/999"))
                .andExpect(status().isNotFound())
                .andExpect(jsonPath("$.title").value("Resource not found"))
                .andExpect(jsonPath("$.detail").value("Product with id 999 was not found"));
    }

    @Test
    void findByPriceRange_returnsPaginatedResponse() throws Exception {
        PageResponse<ProductResponse> response = new PageResponse<>(
                List.of(
                        new ProductResponse(1L, "Mouse", new BigDecimal("799.00")),
                        new ProductResponse(2L, "Keyboard", new BigDecimal("1499.00"))
                ),
                0,
                2,
                5,
                3,
                false
        );

        when(productService.findByPriceRange(new BigDecimal("100.00"), new BigDecimal("2000.00"), 0, 2))
                .thenReturn(response);

        mockMvc.perform(get("/api/products/price-range")
                        .param("minPrice", "100.00")
                        .param("maxPrice", "2000.00")
                        .param("page", "0")
                        .param("size", "2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page").value(0))
                .andExpect(jsonPath("$.size").value(2))
                .andExpect(jsonPath("$.totalElements").value(5))
                .andExpect(jsonPath("$.totalPages").value(3))
                .andExpect(jsonPath("$.last").value(false))
                .andExpect(jsonPath("$.content[0].id").value(1))
                .andExpect(jsonPath("$.content[0].name").value("Mouse"))
                .andExpect(jsonPath("$.content[1].id").value(2))
                .andExpect(jsonPath("$.content[1].name").value("Keyboard"));
    }

    @Test
    void searchAdvanced_returnsPaginatedResponse() throws Exception {
        PageResponse<ProductResponse> response = new PageResponse<>(
                List.of(new ProductResponse(10L, "Laptop Pro", new BigDecimal("24999.00"))),
                1,
                1,
                2,
                2,
                true
        );

        when(productService.searchByNameAndMinPrice("laptop", new BigDecimal("10000.00"), 1, 1))
                .thenReturn(response);

        mockMvc.perform(get("/api/products/search/advanced")
                        .param("name", "laptop")
                        .param("minPrice", "10000.00")
                        .param("page", "1")
                        .param("size", "1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.page").value(1))
                .andExpect(jsonPath("$.size").value(1))
                .andExpect(jsonPath("$.totalElements").value(2))
                .andExpect(jsonPath("$.totalPages").value(2))
                .andExpect(jsonPath("$.last").value(true))
                .andExpect(jsonPath("$.content[0].id").value(10))
                .andExpect(jsonPath("$.content[0].name").value("Laptop Pro"));
    }
}
