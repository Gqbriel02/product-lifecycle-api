package com.example.productlifecycleapi.repository;

import com.example.productlifecycleapi.model.Material;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MaterialRepository extends JpaRepository<Material, String> {
}
