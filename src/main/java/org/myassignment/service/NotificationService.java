package org.myassignment.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.myassignment.entity.Departure;
import org.myassignment.entity.Notification;
import org.myassignment.entity.NotificationStatus;
import org.myassignment.entity.Reservation;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Two steps, so a mail problem never breaks a booking:
 * 1) queue()        - saves a PENDING row in the same transaction as the booking.
 * 2) sendPending()  - a scheduled job sends the PENDING rows through Gmail SMTP.
 */
@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class NotificationService {

    public static final String DEPARTURE_CONFIRMED = "DEPARTURE_CONFIRMED";
    public static final String DEPARTURE_CANCELLED = "DEPARTURE_CANCELLED";

    private final org.myassignment.repository.NotificationRepository notificationRepository;
    private final JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String from;

    @Transactional
    public Notification queue(Reservation reservation, String type) {
        return notificationRepository.save(Notification.builder()
                .reservation(reservation)
                .recipientEmail(reservation.getUser().getEmail())
                .type(type)
                .build());
    }

    public List<Notification> getByReservation(Long reservationId) {
        return notificationRepository.findByReservationId(reservationId);
    }

    @Scheduled(fixedDelayString = "${app.mail.poll-ms:30000}")
    @Transactional
    public void sendPending() {
        for (Notification n : notificationRepository.findByStatus(NotificationStatus.PENDING)) {
            try {
                mailSender.send(buildMessage(n));
                n.setStatus(NotificationStatus.SENT);
                n.setSentAt(LocalDateTime.now());
            } catch (Exception e) {
                log.warn("Failed to send notification {}: {}", n.getId(), e.getMessage());
                n.setStatus(NotificationStatus.FAILED);
            }
        }
    }

    /** Puts FAILED notifications back to PENDING so the next run tries again. */
    @Transactional
    public int retryFailed() {
        List<Notification> failed = notificationRepository.findByStatus(NotificationStatus.FAILED);
        failed.forEach(n -> n.setStatus(NotificationStatus.PENDING));
        return failed.size();
    }

    private SimpleMailMessage buildMessage(Notification n) {
        Reservation r = n.getReservation();
        Departure d = r.getDeparture();
        String title = d.getTravelPackage().getTitle();
        String name = r.getUser().getFullName();

        String subject;
        String body;
        if (DEPARTURE_CANCELLED.equals(n.getType())) {
            subject = "[Mr. World Travel] Departure cancelled: " + title;
            body = "Hello " + name + ",\n\n"
                    + "We are sorry to tell you that the departure on " + d.getDepartureDate()
                    + " for \"" + title + "\" has been cancelled.\n"
                    + "Your reservation (#" + r.getId() + ") has been cancelled as well.\n\n"
                    + "Mr. World Travel";
        } else {
            subject = "[Mr. World Travel] Departure confirmed: " + title;
            body = "Hello " + name + ",\n\n"
                    + "Good news! Your trip \"" + title + "\" departing on " + d.getDepartureDate()
                    + " is confirmed.\n"
                    + "Reservation: #" + r.getId() + "\n"
                    + "Applicants: " + r.getApplicantsCount() + "\n"
                    + "Total price: " + r.getTotalPrice() + "\n\n"
                    + "Mr. World Travel";
        }

        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(from);
        message.setTo(n.getRecipientEmail());
        message.setSubject(subject);
        message.setText(body);
        return message;
    }
}
