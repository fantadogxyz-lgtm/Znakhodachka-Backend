package com.znahodochka.backend.storage.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "attachment_type", schema = "storage")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AttachmentType {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;
}
