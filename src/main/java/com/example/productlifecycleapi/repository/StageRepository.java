package com.example.productlifecycleapi.repository;

import com.example.productlifecycleapi.model.Stage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StageRepository extends JpaRepository<Stage, Long> {
    Optional<Stage> findByName(String name);
}
