package com.example.productlifecycleapi.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "product_stage_history")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductStageHistory {

    @EmbeddedId
    private ProductStageHistoryId id;

    // Map the composite key parts to actual entities
    @ManyToOne
    @MapsId("stageId")
    @JoinColumn(name = "stage_id")
    private Stage stage;

    @ManyToOne
    @MapsId("productId")
    @JoinColumn(name = "product_id")
    private Product product;

    // The user who started the stage
    @ManyToOne
    @JoinColumn(name = "user_id")
    private AppUser user;  // See the user entity below
}
