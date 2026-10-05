package com.znahodochka.backend.publications.model;

import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "tags", schema = "publications")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
//@JsonFormat(shape = JsonFormat.Shape.NUMBER)
public class Tag {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String title;

//    @com.fasterxml.jackson.annotation.JsonValue
//    public Long toJson() {
//        return this.id;
//    }
}
