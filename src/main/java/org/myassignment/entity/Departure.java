package org.myassignment.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "departure")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Departure {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "departure_id")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "package_id", nullable = false)
    private TravelPackage travelPackage;

    @Column(name = "departure_date", nullable = false)
    private LocalDate departureDate;

    // "Confirm Departure Status (3+ Applicants)"
    @Column(name = "min_applicants", nullable = false)
    @Builder.Default
    private Integer minApplicants = 3;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 20)
    @Builder.Default
    private DepartureStatus status = DepartureStatus.OPEN;

    @OneToMany(mappedBy = "departure")
    @ToString.Exclude
    @Builder.Default
    private List<Reservation> reservations = new ArrayList<>();

    /** Total applicants across all non-cancelled reservations. */
    public int totalApplicants() {
        return reservations.stream()
                .filter(r -> r.getStatus() != ReservationStatus.CANCELLED)
                .mapToInt(Reservation::getApplicantsCount)
                .sum();
    }

    /** Switches OPEN -> CONFIRMED once the minimum is reached. Returns true if it just changed. */
    public boolean confirmIfEligible() {
        if (status == DepartureStatus.OPEN && totalApplicants() >= minApplicants) {
            status = DepartureStatus.CONFIRMED;
            return true;
        }
        return false;
    }
}
