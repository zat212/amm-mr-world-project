package org.myassignment.entity;


import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "tier")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Tier {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tier_id")
    private Long id;

    // e.g. Standard / Premium / Luxury
    @Column(nullable = false, unique = true, length = 50)
    private String name;

    @OneToMany(mappedBy = "tier")
    @ToString.Exclude
    @Builder.Default
    private List<TravelPackage> packages = new ArrayList<>();
}