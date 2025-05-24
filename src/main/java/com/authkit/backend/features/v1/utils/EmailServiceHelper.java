package com.authkit.backend.features.v1.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.MailException;
import org.springframework.stereotype.Service;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.MessagingException;

@Service
public class EmailServiceHelper {

    @Autowired
    private JavaMailSender mailSender;

    private static final String FROM_EMAIL = "noreply@marcoswiendl.com";
    private static final String FROM_NAME = "AuthKit";

    public void sendEmail(String to, String subject, String text) throws MailException {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(FROM_NAME + " <" + FROM_EMAIL + ">");
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        mailSender.send(message);
    }

    public void sendHtmlEmail(String to, String subject, String htmlText) throws MailException, MessagingException {
        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);
        helper.setFrom(FROM_NAME + " <" + FROM_EMAIL + ">");
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(htmlText, true);
        mailSender.send(mimeMessage);
    }

    public void sendVerificationEmail(String to, String name, String verificationUrl, String verificationCode) throws MailException, MessagingException {
        String subject = "Verify your email address";
        String htmlContent = String.format("""
            <html>
            <head>
                <style>
                    body {
                        font-family: Arial, sans-serif;
                        margin: 0;
                        padding: 0;
                        background-color: #f4f4f4;
                    }
                    .container {
                        max-width: 600px;
                        margin: 20px auto;
                        background-color: #ffffff;
                        border-radius: 8px;
                        overflow: hidden;
                        box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
                    }
                    .header {
                        background-color: #000000;
                        color: #ffffff;
                        padding: 16px 24px;
                        font-size: 20px;
                        font-weight: bold;
                    }
                    .content {
                        padding: 24px;
                        color: #333333;
                    }
                    h1 {
                        color: #111111;
                        font-size: 24px;
                        margin-top: 0;
                    }
                    p {
                        line-height: 1.6;
                    }
                    .button {
                        display: inline-block;
                        margin-top: 20px;
                        padding: 12px 24px;
                        background-color: #007bff;
                        color: #ffffff;
                        text-decoration: none;
                        border-radius: 4px;
                        font-weight: bold;
                    }
                    .code-box {
                        background-color: #f0f0f0;
                        border: 1px dashed #999;
                        padding: 10px;
                        font-size: 18px;
                        text-align: center;
                        margin: 16px 0;
                        font-weight: bold;
                    }
                    .footer {
                        font-size: 12px;
                        color: #999999;
                        text-align: center;
                        padding: 16px;
                    }
                </style>
            </head>
            <body>
                <div class="container">
                    <div class="header">AuthKit</div>
                    <div class="content">
                        <h1>Welcome to AuthKit, %s!</h1>
                        <p>Thank you for registering. To complete your registration, please verify your email address by clicking the button below:</p>
                        <p><a href="%s" class="button">Verify Email Address</a></p>
                        <p>Or use this verification code:</p>
                        <div class="code-box">%s</div>
                        <p>This link will expire in 24 hours.</p>
                        <p>If you did not create an account, please ignore this email.</p>
                    </div>
                    <div class="footer">© 2025 AuthKit. All rights reserved.</div>
                </div>
            </body>
            </html>
            """, name, verificationUrl, verificationCode);
    
        sendHtmlEmail(to, subject, htmlContent);
    }
}
