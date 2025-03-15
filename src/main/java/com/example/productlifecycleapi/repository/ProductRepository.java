package com.example.productlifecycleapi.repository;

import com.example.productlifecycleapi.model.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
