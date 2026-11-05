package org.myassignment.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "theme")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Theme {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "theme_id")
    private Long id;

    // Honeymoon / Filial Piety / Golf / Trekking
    @Column(nullable = false, unique = true, length = 50)
    private String name;

    @OneToMany(mappedBy = "theme")
    @ToString.Exclude
    @Builder.Default
    private List<TravelPackage> packages = new ArrayList<>();
}
