package com.example.productlifecycleapi.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "bom")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Bom {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
}
