package com.bookit.bookit.service.notifications;

import com.bookit.bookit.entity.bokning.Bokning;
import com.bookit.bookit.entity.notifications.Notifications;
import com.bookit.bookit.entity.user.UserEntity;
import com.bookit.bookit.enums.StädningsAlternativ;
import com.bookit.bookit.repository.notifications.NotificationsRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@AllArgsConstructor
@EnableJpaRepositories("com.bookit.bookit.repository.notifications")
public class NotificationsService {

    private final JavaMailSender javaMailSender;
    private final NotificationsRepository notificationsRepository; // Inject your Notification repository

    //@Transactional
    public void sendEmail(String to, String subject, String body, StädningsAlternativ serviceType, UserEntity user, Bokning booking) {
        // Create and save the notification in the database
        Notifications notification = new Notifications();
        notification.setMeddelande(body);
        notification.setSubject(subject);
        notification.setUser(user);
        notification.setTimestamp(LocalDateTime.now());
        notification.setIsSent(false);
        notification.setIsRead(false);

        notification.setBokning(booking);
        notificationsRepository.save(notification);

        // Append service type to the email body
        body += "\nService Type: " + serviceType.toString();
        body += "\nAddress: " + booking.getAdress();

        // Send the email
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
            // Log the exception
            System.err.println("Error sending email: " + e.getMessage());
            // You might also want to notify an admin or take other actions
        }
    }


    public void sendRegistrationEmail(String to, String subject, String body, UserEntity user) {
        // Create the notification entity
        Notifications notification = new Notifications();
        notification.setMeddelande(body);
        notification.setSubject(subject);
        notification.setUser(user);
        notification.setTimestamp(LocalDateTime.now());
        notification.setIsSent(false);
        notification.setIsRead(false);
        notification.setBokning(null); // Explicitly set to null for registration emails

        // Save the notification entity to the database
        notificationsRepository.save(notification);

        // Create the email message
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(to);
        msg.setSubject(subject);
        msg.setText(body);

        try {
            // Attempt to send the email
            javaMailSender.send(msg);
            // Update the notification as sent
            notification.setIsSent(true);
            notificationsRepository.save(notification);
        } catch (MailException e) {
            // Log the exception for failed email sending
            System.err.println("Error sending registration email: " + e.getMessage());
            // Additional error handling can be implemented here
        }
    }


    // Additional methods to mark a notification as read, delete, etc. can be added here
}
