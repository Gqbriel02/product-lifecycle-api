package com.example.productlifecycleapi.repository;

import com.example.productlifecycleapi.model.Bom;
import org.springframework.data.jpa.repository.JpaRepository;

public interface BomRepository extends JpaRepository<Bom, Long> {
}
