package com.znahodochka.backend.publications.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "place_categories", schema = "publications")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Category {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
}
