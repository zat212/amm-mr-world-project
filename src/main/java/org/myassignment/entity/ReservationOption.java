package org.myassignment.entity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "reservation_option")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ReservationOption {

    @EmbeddedId
    private ReservationOptionId id;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("reservationId")
    @JoinColumn(name = "reservation_id")
    @ToString.Exclude
    private Reservation reservation;

    @ManyToOne(fetch = FetchType.LAZY)
    @MapsId("optionId")
    @JoinColumn(name = "option_id")
    @ToString.Exclude
    private CustomizeOption option;
}