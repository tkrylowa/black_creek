package ru.spring.tkrylova.blackcreek.service;

import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import ru.spring.tkrylova.blackcreek.servce.EmailService;

import java.util.Objects;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@SpringBootTest
public class EmailServiceTest {
    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private EmailService emailService;

    @Test
    void SendEmail_ShouldSendEmailWithCorrectDetails() {
        String toEmail = "test@example.com";
        String subject = "Test Subject";
        String body = "This is a test email.";

        emailService.sendEmail(toEmail, subject, body);

        ArgumentCaptor<SimpleMailMessage> messageCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender, times(1)).send(messageCaptor.capture());

        SimpleMailMessage capturedMessage = messageCaptor.getValue();
        assertNotNull(capturedMessage, "The captured message should not be null");
        assertEquals(toEmail, Objects.requireNonNull(capturedMessage.getTo())[0], "The recipient email should match");
        assertEquals(subject, capturedMessage.getSubject(), "The email subject should match");
        assertEquals(body, capturedMessage.getText(), "The email body should match");
    }

    @Test
    void sendEmail_WithNullRecipient_ShouldThrowException() {
        String toEmail = null;
        String subject = "Test Subject";
        String body = "This is a test email.";

        assertThrows(IllegalArgumentException.class, () ->
                emailService.sendEmail(toEmail, subject, body), "Sending an email with a null recipient should throw an IllegalArgumentException");

        verify(mailSender, never()).send(any(SimpleMailMessage.class));
    }

    @Test
    void sendEmail_WithEmptyRecipient_ShouldThrowException() {
        String toEmail = "";
        String subject = "Test Subject";
        String body = "This is a test email.";

        assertThrows(IllegalArgumentException.class, () ->
                emailService.sendEmail(toEmail, subject, body), "Sending an email with a null recipient should throw an IllegalArgumentException");

        verify(mailSender, never()).send(any(SimpleMailMessage.class));
    }

    @Test
    void sendEmail_WithBlankRecipient_ShouldThrowException() {
        String toEmail = "  ";
        String subject = "Test Subject";
        String body = "This is a test email.";

        assertThrows(IllegalArgumentException.class, () ->
                emailService.sendEmail(toEmail, subject, body), "Sending an email with a null recipient should throw an IllegalArgumentException");

        verify(mailSender, never()).send(any(SimpleMailMessage.class));
    }

    @Test
    void sendEmail_WithWrongFormatRecipient_ShouldThrowException() {
        String toEmail = "invalid-email";
        String subject = "Test Subject";
        String body = "This is a test email.";

        assertThrows(IllegalArgumentException.class, () ->
                emailService.sendEmail(toEmail, subject, body), "Sending an email with a null recipient should throw an IllegalArgumentException");

        verify(mailSender, never()).send(any(SimpleMailMessage.class));
    }

    @Test
    void sendEmail_WithEmptySubject_ShouldSendEmail() {
        String toEmail = "test@example.com";
        String subject = "";
        String body = "This is a test email.";

        emailService.sendEmail(toEmail, subject, body);

        ArgumentCaptor<SimpleMailMessage> messageCaptor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender, times(1)).send(messageCaptor.capture());

        SimpleMailMessage capturedMessage = messageCaptor.getValue();
        assertNotNull(capturedMessage, "The captured message should not be null");
        assertEquals(toEmail, Objects.requireNonNull(capturedMessage.getTo())[0], "The recipient email should match");
        assertEquals(subject, capturedMessage.getSubject(), "The email subject should be empty");
        assertEquals(body, capturedMessage.getText(), "The email body should match");
    }
}
