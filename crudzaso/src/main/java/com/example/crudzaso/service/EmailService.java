package com.example.crudzaso.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired(required = false)
    private JavaMailSender javaMailSender;

    public void sendEmail(String to, String subject, String mesage) {
        if (javaMailSender == null) {
            System.out.println("Mail not configured, skipping email to " + to + " - " + subject);
            return;
        }
        try {
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("no-reply@crudzaso.com");
            message.setTo(to);
            message.setSubject(subject);
            message.setText(mesage);
            javaMailSender.send(message);
        } catch (Exception e) {
            System.out.println("Error sending email: " + e.getMessage());
        }
    }
}
