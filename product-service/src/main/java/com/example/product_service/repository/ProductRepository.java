package com.example.product_service.repository;

import com.example.product_service.model.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {
    // Search by Name or Code with Pagination
    Page<Product> findByNameContainingOrCodeContaining(String name, String code, Pageable pageable);
}
