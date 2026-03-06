package com.lead.productservice.product.domain.repository;

import com.lead.productservice.product.domain.entity.ProductEntity;
import java.math.BigDecimal;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.boot.jdbc.test.autoconfigure.AutoConfigureTestDatabase;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.ANY)
class ProductRepositoryTest {

    @Autowired
    private ProductRepository productRepository;

    @Test
    void findByNameContainingIgnoreCase_returnsMatchesIgnoringCase() {
        productRepository.save(new ProductEntity("Mechanical Keyboard", new BigDecimal("1500.00")));
        productRepository.save(new ProductEntity("Gaming Mouse", new BigDecimal("800.00")));

        List<ProductEntity> result = productRepository.findByNameContainingIgnoreCase("KEYBOARD");

        assertEquals(1, result.size());
        assertEquals("Mechanical Keyboard", result.get(0).getName());
    }

    @Test
    void existsByNameIgnoreCase_returnsTrueWhenNameExists() {
        productRepository.save(new ProductEntity("Monitor", new BigDecimal("3200.00")));

        boolean exists = productRepository.existsByNameIgnoreCase("monitor");

        assertTrue(exists);
        assertFalse(productRepository.existsByNameIgnoreCase("headphones"));
    }

    @Test
    void findByPriceBetweenOrderByPriceAsc_returnsSortedPage() {
        productRepository.save(new ProductEntity("Entry", new BigDecimal("100.00")));
        productRepository.save(new ProductEntity("Pro", new BigDecimal("400.00")));
        productRepository.save(new ProductEntity("Ultra", new BigDecimal("300.00")));

        Page<ProductEntity> page = productRepository.findByPriceBetweenOrderByPriceAsc(
                new BigDecimal("150.00"),
                new BigDecimal("450.00"),
                PageRequest.of(0, 10, Sort.unsorted())
        );

        assertEquals(2, page.getTotalElements());
        assertEquals("Ultra", page.getContent().get(0).getName());
        assertEquals("Pro", page.getContent().get(1).getName());
    }

    @Test
    void searchByNameAndMinPrice_filtersByBothCriteria() {
        productRepository.save(new ProductEntity("Laptop Basic", new BigDecimal("900.00")));
        productRepository.save(new ProductEntity("Laptop Pro", new BigDecimal("2100.00")));
        productRepository.save(new ProductEntity("Mouse", new BigDecimal("200.00")));

        Page<ProductEntity> page = productRepository.searchByNameAndMinPrice(
                "laptop",
                new BigDecimal("1000.00"),
                PageRequest.of(0, 10)
        );

        assertEquals(1, page.getTotalElements());
        assertEquals("Laptop Pro", page.getContent().get(0).getName());
    }
}
