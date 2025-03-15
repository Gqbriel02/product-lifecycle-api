package com.example.productlifecycleapi.repository;

import com.example.productlifecycleapi.model.ProductStageHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface ProductStageHistoryRepository extends JpaRepository<ProductStageHistory, Long> {

    @Query("SELECT psh FROM ProductStageHistory psh WHERE psh.product.id = :productId ORDER BY psh.id.startOfStage DESC LIMIT 1")
    Optional<ProductStageHistory> findLatestStageByProductId(@Param("productId") Long productId);

    @Modifying
    @Query("DELETE FROM ProductStageHistory psh WHERE psh.product.id = :productId")
    void deleteByProductId(@Param("productId") Long productId);

    List<ProductStageHistory> findByProductId(Long productId);
}
