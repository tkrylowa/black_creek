package ru.spring.tkrylova.blackcreek.servce;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import java.util.regex.Pattern;

@Service
public class EmailService {
    private final JavaMailSender mailSender;

    private static final Pattern EMAIL_PATTERN = Pattern.compile(
            "^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$"
    );

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendEmail(String toEmail, String subject, String body) {
        if (toEmail == null || toEmail.isEmpty() || toEmail.isBlank()) {
            throw new IllegalArgumentException("Email is null!");
        }
        if (!EMAIL_PATTERN.matcher(toEmail).matches()) {
            throw new IllegalArgumentException("Invalid email format!");
        }
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject(subject);
        message.setText(body);
        mailSender.send(message);
    }
}
