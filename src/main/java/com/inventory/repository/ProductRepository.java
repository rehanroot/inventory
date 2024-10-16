package com.inventory.repository;

import com.inventory.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    List<Product> findByNameContainingOrVendorNameContainingOrCategoryContaining(String name, String vendorName, String category);
}

