package com.example.productlifecycleapi.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "bom_material")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BomMaterial {

    @EmbeddedId
    private BomMaterialId id;

    private Double qty;

    @Column(name = "unit_measure_code")
    private String unitMeasureCode;

    @ManyToOne
    @MapsId("bomId")
    @JoinColumn(name = "bom_id")
    private Bom bom;

    @ManyToOne
    @MapsId("materialNumber")
    @JoinColumn(name = "material_number")
    private Material material;
}
