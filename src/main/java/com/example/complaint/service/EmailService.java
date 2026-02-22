package com.example.complaint.service;

import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.*;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendOtpEmail(String toEmail, String otp) throws Exception {

        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper =
                new MimeMessageHelper(message, true);

        String htmlContent =
                "<div style='font-family:Arial;padding:20px'>" +
                        "<h2 style='color:#2c3e50'>Campus Connect Verification</h2>" +
                        "<p>Your OTP Code is:</p>" +
                        "<h1 style='color:#2980b9'>" + otp + "</h1>" +
                        "<p>This OTP will expire in 5 minutes.</p>" +
                        "<br><p>If you did not request this, ignore this email.</p>" +
                        "</div>";

        helper.setTo(toEmail);
        helper.setSubject("Campus Connect OTP Verification");
        helper.setText(htmlContent, true);

        mailSender.send(message);
    }
}
