package org.myassignment.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.*;

import java.io.Serializable;

@Embeddable
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode
public class ReservationOptionId implements Serializable {

    @Column(name = "reservation_id")
    private Long reservationId;

    @Column(name = "option_id")
    private Long optionId;
}