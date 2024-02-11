package com.bookit.bookit.service.notifications;

import com.bookit.bookit.entity.bokning.Bokning;
import com.bookit.bookit.entity.faktura.Faktura;
import com.bookit.bookit.entity.notifications.Notifications;
import com.bookit.bookit.entity.user.UserEntity;


import com.bookit.bookit.enums.StädningsAlternativ;
import com.bookit.bookit.repository.notifications.NotificationsRepository;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.AllArgsConstructor;
import org.springframework.core.io.FileSystemResource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;


import java.io.File;
import java.time.LocalDateTime;


@Service
@AllArgsConstructor
@EnableJpaRepositories("com.bookit.bookit.repository.notifications")
public class NotificationsService {

    private final JavaMailSender javaMailSender;
    private final NotificationsRepository notificationsRepository;

    public void sendEmail(String to, String subject, String body, StädningsAlternativ serviceType, UserEntity user, Bokning booking) {
        Notifications notification = createAndSaveNotification(body, subject, user, booking);

        // Append service type to the email body
        body += "\nTjänsttyp: " + serviceType.toString();
        body += "\nAdress: " + booking.getAdress();

        // Send the email
        sendEmailMessage(to, subject, body, notification);
    }

    //Booking är vid registrering av ett kundkonto null eftersom det finns ingen bokning då.
    public void sendRegistrationEmail(String to, String subject, String body, UserEntity user) {
        Notifications notification = createAndSaveNotification(body, subject, user, null);

        // Send the email
        sendEmailMessage(to, subject, body, notification);
    }


    public void sendEmailWithAttachment(String to, String subject, String body, File attachment, Faktura faktura) {
        try {
            MimeMessage message = javaMailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true);

            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(body);

            FileSystemResource file = new FileSystemResource(attachment);
            helper.addAttachment(attachment.getName(), file);

            javaMailSender.send(message);

            // Create and save notification
            Notifications notification = new Notifications();
            // Set notification fields...
            notification.setFaktura(faktura);
            notificationsRepository.save(notification);
        } catch (MessagingException | MailException e) {
            System.err.println("Error sending email with attachment: " + e.getMessage());
            // Additional error handling
        }
    }


    //Ska raderas
    public void sendBookingConfirmationEmail(String to, String subject, String body, UserEntity user, Bokning booking) {
        Notifications notification = createAndSaveNotification(body, subject, user, booking);

        // Append booking details to the email body
        body += "\nTjänsttyp: " + booking.getTjänst().getStädningsAlternativ().toString();
        body += "\nAdress: " + booking.getAdress();
        body += "\nTidslucka: " + booking.getBookingTime().toString() + " till " + booking.getEndTime().toString();

        // Send the email
        sendEmailMessage(to, subject, body, notification);
    }

    // Helper method to create and save a notification
    private Notifications createAndSaveNotification(String body, String subject, UserEntity user, Bokning booking) {
        Notifications notification = new Notifications();
        notification.setMeddelande(body);
        notification.setSubject(subject);
        notification.setUser(user);
        notification.setTimestamp(LocalDateTime.now());
        notification.setIsSent(false);
        notification.setIsRead(false);
        notification.setBokning(booking);

        return notificationsRepository.save(notification);
    }

    // Helper method to send an email message and update notification status
    private void sendEmailMessage(String to, String subject, String body, Notifications notification) {
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(to);
        msg.setSubject(subject);
        msg.setText(body);

        try {
            javaMailSender.send(msg);
            // Update the notification as sent
            notification.setIsSent(true);
            notificationsRepository.save(notification);
        } catch (MailException e) {
            System.err.println("Fel vid skickande av e-post: " + e.getMessage());
            // Additional error handling can be implemented here
        }
    }

    // Additional methods to mark a notification as read, delete, etc. can be added here
}














