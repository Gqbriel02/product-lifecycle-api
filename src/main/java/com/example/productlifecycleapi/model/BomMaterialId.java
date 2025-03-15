package com.example.productlifecycleapi.model;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BomMaterialId implements Serializable {
    private Long bomId;
    private String materialNumber;
}
