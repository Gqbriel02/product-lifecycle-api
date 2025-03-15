package com.example.productlifecycleapi.dto;

import lombok.*;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProductCreationRequest {
    private String name;
    private String description;
    private Double estimatedHeight;
    private Double estimatedWidth;
    private Double estimatedWeight;
    private List<MaterialSelection> materials;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MaterialSelection {
        private String materialNumber;
        private Double qty;
        private String unitMeasureCode;
    }
}
