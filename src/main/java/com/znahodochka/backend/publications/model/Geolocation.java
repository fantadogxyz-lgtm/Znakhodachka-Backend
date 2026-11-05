package com.znahodochka.backend.publications.model;

import jakarta.persistence.*;
import lombok.*;
import org.locationtech.jts.geom.Point;
import java.util.UUID;

@Entity
@Table(name = "geolocations", schema = "publications")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Geolocation {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    private String title;
    private Double latitude;
    private Double longitude;

    @Column(columnDefinition = "geography(Point, 4326)")
    private Point location;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "discussion_id")
    private Discussion discussion;
}