package org.myassignment.entity;


import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "customize_option")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomizeOption {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "option_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "package_id", nullable = false)
    private TravelPackage travelPackage;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    private OptionCategory category;

    @Column(nullable = false, length = 150)
    private String name;

    @Column(name = "extra_price", nullable = false, precision = 10, scale = 2)
    @Builder.Default
    private BigDecimal extraPrice = BigDecimal.ZERO;

    @OneToMany(mappedBy = "option")
    @ToString.Exclude
    @Builder.Default
    private List<ReservationOption> reservationOptions = new ArrayList<>();
}

