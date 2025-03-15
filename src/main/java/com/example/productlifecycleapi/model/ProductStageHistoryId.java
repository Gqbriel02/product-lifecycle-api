package com.example.productlifecycleapi.model;

import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;
import java.time.LocalDateTime;

@Embeddable
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductStageHistoryId implements Serializable {

    private Long stageId;
    private Long productId;
    private LocalDateTime startOfStage;
}
