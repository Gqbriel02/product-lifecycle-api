package com.example.productlifecycleapi.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "material")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Material {

    @Id
    @Column(name = "material_number")
    private String materialNumber;

    @Column(name = "material_description")
    private String materialDescription;

    private Double weight;
    private Double width;
    private Double height;
}
