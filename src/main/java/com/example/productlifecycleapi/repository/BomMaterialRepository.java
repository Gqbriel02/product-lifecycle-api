package com.example.productlifecycleapi.repository;

import com.example.productlifecycleapi.model.BomMaterial;
import com.example.productlifecycleapi.model.BomMaterialId;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BomMaterialRepository extends JpaRepository<BomMaterial, BomMaterialId> {
    List<BomMaterial> findByBomId(Long id);

    @Modifying
    @Query("DELETE FROM BomMaterial bm WHERE bm.bom.id = :bomId")
    void deleteByBomId(@Param("bomId") Long bomId);
}
