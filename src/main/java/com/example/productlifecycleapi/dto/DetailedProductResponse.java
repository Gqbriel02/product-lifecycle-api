package com.example.productlifecycleapi.dto;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@AllArgsConstructor
public class DetailedProductResponse {
    private Long id;
    private String name;
    private String description;
    private Double estimatedHeight;
    private Double estimatedWidth;
    private Double estimatedWeight;
    private String stage;
    private List<Map<String, Object>> materials;
}
