package com.example.productlifecycleapi.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class ProductResponse {
    private Long id;
    private String name;
    private String description;
    private Double estimatedHeight;
    private Double estimatedWidth;
    private Double estimatedWeight;
    private String stage;
}
