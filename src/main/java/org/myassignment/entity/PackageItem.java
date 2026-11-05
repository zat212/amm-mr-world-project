package org.myassignment.entity;


import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "package_item")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PackageItem {

    @EmbeddedId
    private PackageItemId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("packageId")
    @JoinColumn(name = "package_id")
    @ToString.Exclude
    private TravelPackage travelPackage;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("itemId")
    @JoinColumn(name = "item_id")
    @ToString.Exclude
    private TravelItem item;

    @Column(nullable = false)
    @Builder.Default
    private Integer quantity = 1;
}
