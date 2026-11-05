package org.myassignment.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "travel_item")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TravelItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "item_id")
    private Long id;

    // Couple T-shirts / Ginseng / Golf Balls ...
    @Column(nullable = false, length = 150)
    private String name;

    @Column(length = 50)
    private String category;

    @Column(precision = 10, scale = 2)
    private BigDecimal price;

    @Column(name = "stock_qty", nullable = false)
    @Builder.Default
    private Integer stockQty = 0;

    @OneToMany(mappedBy = "item")
    @ToString.Exclude
    @Builder.Default
    private List<PackageItem> packageItems = new ArrayList<>();
}
