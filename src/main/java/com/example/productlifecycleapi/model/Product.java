package com.example.productlifecycleapi.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "product")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String description;

    @Column(name = "estimated_height")
    private Double estimatedHeight;

    @Column(name = "estimated_width")
    private Double estimatedWidth;

    @Column(name = "estimated_weight")
    private Double estimatedWeight;

    // Relationship to BOM
    @ManyToOne
    @JoinColumn(name = "bom_id")
    private Bom bom;
}
