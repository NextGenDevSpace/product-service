package com.lead.productservice.product.domain.repository;

import com.lead.productservice.product.domain.entity.ProductEntity;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

public interface ProductRepository extends JpaRepository<ProductEntity, Long> {

    List<ProductEntity> findByNameContainingIgnoreCase(String name);

        boolean existsByNameIgnoreCase(String name);

        Page<ProductEntity> findByPriceBetweenOrderByPriceAsc(BigDecimal minPrice, BigDecimal maxPrice, Pageable pageable);

        @Query("""
                        select p
                        from ProductEntity p
                        where lower(p.name) like lower(concat('%', :name, '%'))
                            and p.price >= :minPrice
                        """)
        Page<ProductEntity> searchByNameAndMinPrice(
                        @Param("name") String name,
                        @Param("minPrice") BigDecimal minPrice,
                        Pageable pageable
        );
}
