package org.myassignment.repository;

import org.myassignment.entity.Notification;
import org.myassignment.entity.NotificationStatus;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface NotificationRepository extends JpaRepository<Notification, Long> {

    List<Notification> findByReservationId(Long reservationId);

    // For retrying failed Gmail notifications
    List<Notification> findByStatus(NotificationStatus status);
}
