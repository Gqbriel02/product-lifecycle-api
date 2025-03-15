package com.example.productlifecycleapi.dto;

import lombok.Data;
import lombok.AllArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class StageHistoryResponse {
    private String stageName;
    private LocalDateTime startOfStage;
    private Long userId;
    private String fullName;
}
