package com.example.carrentalsystem.service;

import com.example.carrentalsystem.model.Booking;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;
    private static final Logger logger = LoggerFactory.getLogger(EmailService.class);

    public boolean sendBookingEmail(String recipientEmail, Booking booking) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            
            helper.setTo(recipientEmail);
            helper.setSubject("Booking Confirmation");
            helper.setFrom("velox@carrental.com"); // Set From field

            // Create HTML email content
            String emailContent = "<html><body>"
                    + "<h2>Your Booking is Confirmed</h2>"
                    + "<p>Dear " + booking.getUser().getName() + ",</p>"
                    + "<p>Thank you for booking with us. Here are your booking details:</p>"
                    + "<table border='1' cellpadding='5' cellspacing='0'>"
                    + "<tr><td><b>Booking ID</b></td><td>" + booking.getId() + "</td></tr>"
                    + "<tr><td><b>Car</b></td><td>" + booking.getCar().getMake() + " - " + booking.getCar().getModel() + " (" + booking.getCar().getYear() + ")</td></tr>"
                    + "<tr><td><b>Start Date</b></td><td>" + booking.getStartDate() + "</td></tr>"
                    + "<tr><td><b>End Date</b></td><td>" + booking.getEndDate() + "</td></tr>"
                    + "<tr><td><b>Total Price</b></td><td>$" + booking.getTotalPrice() + "</td></tr>"
                    + "<tr><td><b>Status</b></td><td>" + booking.getStatus() + "</td></tr>"
                    + "</table>"
                    + "<br/><p>We look forward to serving you!</p>"
                    + "<p>Best regards,<br/>Car Rental Team</p>"
                    + "</body></html>";

            helper.setText(emailContent, true); // Set email as HTML

            mailSender.send(message);
            logger.info("Booking email sent successfully to {}", recipientEmail);
            return true;
        } catch (MessagingException ex) {
            logger.error("Failed to send booking email to {}: {}", recipientEmail, ex.getMessage());
            return false;
        }
    }


    public boolean sendCancellationEmail(String recipientEmail, Booking booking) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setFrom("velox@carrental.com"); // Set From field
            helper.setTo(recipientEmail);
            helper.setSubject("Booking Cancellation");

            // Create HTML email content
            String emailContent = "<html><body>"
                    + "<h2>Booking Cancellation</h2>"
                    + "<p>Dear " + booking.getUser().getName() + ",</p>"
                    + "<p>We regret to inform you that your booking has been canceled. Here are the details:</p>"
                    + "<table border='1' cellpadding='5' cellspacing='0'>"
                    + "<tr><td><b>Booking ID</b></td><td>" + booking.getId() + "</td></tr>"
                    + "<tr><td><b>Car</b></td><td>" + booking.getCar().getMake() + " - " + booking.getCar().getModel() + " (" + booking.getCar().getYear() + ")</td></tr>"
                    + "<tr><td><b>Start Date</b></td><td>" + booking.getStartDate() + "</td></tr>"
                    + "<tr><td><b>End Date</b></td><td>" + booking.getEndDate() + "</td></tr>"
                    + "<tr><td><b>Total Price</b></td><td>$" + booking.getTotalPrice() + "</td></tr>"
                    + "<tr><td><b>Status</b></td><td><span style='color:red;'><b>Canceled</b></span></td></tr>"
                    + "</table>"
                    + "<br/><p>If this was a mistake or you need further assistance, please contact our support team.</p>"
                    + "<p>Best regards,<br/>Car Rental Team</p>"
                    + "</body></html>";

            helper.setText(emailContent, true); // Set email as HTML

            mailSender.send(message);
            logger.info("Cancellation email sent successfully to {}", recipientEmail);
            return true;
        } catch (MessagingException ex) {
            logger.error("Failed to send cancellation email to {}: {}", recipientEmail, ex.getMessage());
            return false;
        }
    }

}
