package com.example.productlifecycleapi.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "stages")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Stage {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private String description;
}
