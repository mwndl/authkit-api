package com.authkit.backend.infrastructure.utils;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.MailException;
import org.springframework.stereotype.Service;
import jakarta.mail.internet.MimeMessage;
import jakarta.mail.MessagingException;
import com.authkit.backend.shared.exception.ApiException;
import com.authkit.backend.shared.exception.ApiErrorCode;

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
        String subject = "Verify Your Email Address";
        String htmlContent = String.format("""
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <style>
                    body { 
                        font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
                        line-height: 1.6;
                        margin: 0;
                        padding: 0;
                        background-color: hsl(20 14.3%% 4.1%%);
                        color: hsl(60 9.1%% 97.8%%);
                    }
                    .container { 
                        max-width: 600px;
                        margin: 0 auto;
                        padding: 2rem;
                        background-color: hsl(20 14.3%% 4.1%%);
                    }
                    .email-wrapper {
                        background-color: hsl(12 6.5%% 15.1%%);
                        border-radius: 1rem;
                        overflow: hidden;
                        box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1), 0 2px 4px -1px rgba(0, 0, 0, 0.06);
                    }
                    .header { 
                        background-color: hsl(60 9.1%% 97.8%%);
                        color: hsl(20 14.3%% 4.1%%);
                        padding: 2.5rem 2rem;
                        text-align: center;
                    }
                    .content { 
                        padding: 2.5rem 2rem;
                    }
                    .button { 
                        display: inline-block;
                        padding: 1rem 2rem;
                        background-color: hsl(60 9.1%% 97.8%%);
                        color: hsl(20 14.3%% 4.1%%);
                        text-decoration: none;
                        border-radius: 0.75rem;
                        font-weight: 600;
                        font-size: 1.125rem;
                        margin: 1.5rem 0;
                        text-align: center;
                        min-width: 240px;
                        box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1), 0 2px 4px -1px rgba(0, 0, 0, 0.06);
                    }
                    .code-box { 
                        background-color: hsl(20 14.3%% 4.1%%);
                        padding: 1.5rem;
                        text-align: center;
                        font-size: 2rem;
                        font-weight: 700;
                        margin: 2rem 0;
                        border-radius: 0.75rem;
                        letter-spacing: 0.5rem;
                        color: hsl(60 9.1%% 97.8%%);
                        box-shadow: inset 0 2px 4px 0 rgba(0, 0, 0, 0.06);
                    }
                    .footer { 
                        text-align: center;
                        padding: 2rem;
                        color: hsl(24 5.4%% 63.9%%);
                        font-size: 0.875rem;
                        background-color: hsl(12 6.5%% 15.1%%);
                        border-top: 1px solid hsl(12 6.5%% 20.1%%);
                    }
                    h1 {
                        margin: 0;
                        font-size: 1.75rem;
                        font-weight: 700;
                        letter-spacing: -0.025em;
                    }
                    p {
                        margin: 1.25rem 0;
                        color: hsl(60 9.1%% 97.8%%);
                        font-size: 1.125rem;
                    }
                    .logo {
                        font-size: 2rem;
                        font-weight: 800;
                        margin-bottom: 1.5rem;
                        letter-spacing: -0.05em;
                    }
                    .divider {
                        height: 1px;
                        background-color: hsl(12 6.5%% 20.1%%);
                        margin: 2rem 0;
                    }
                    table {
                        width: 100%%;
                        background-color: hsl(20 14.3%% 4.1%%);
                    }
                    td {
                        padding: 0;
                    }
                    .button-wrapper {
                        text-align: center;
                        margin: 2rem 0;
                    }
                    .expiry-notice {
                        font-size: 0.875rem;
                        color: hsl(24 5.4%% 63.9%%);
                        margin-top: 1rem;
                    }
                </style>
            </head>
            <body>
                <table role="presentation" cellspacing="0" cellpadding="0" border="0">
                    <tr>
                        <td align="center">
                            <div class="container">
                                <div class="email-wrapper">
                                    <div class="header">
                                        <div class="logo">AuthKit</div>
                                        <h1>Welcome, %s!</h1>
                                    </div>
                                    <div class="content">
                                        <p>Thank you for registering. To complete your registration, please verify your email address by clicking the button below:</p>
                                        
                                        <div class="button-wrapper">
                                            <a href="%s" class="button">Verify Email Address</a>
                                        </div>

                                        <div class="divider"></div>

                                        <p>Or use this verification code:</p>
                                        <div class="code-box">%s</div>
                                        
                                        <p class="expiry-notice">This link and code will expire in 24 hours.</p>
                                        <p>If you did not create an account, please ignore this email.</p>
                                    </div>
                                    <div class="footer">
                                        <p>This is an automated message, please do not reply to this email.</p>
                                    </div>
                                </div>
                            </div>
                        </td>
                    </tr>
                </table>
            </body>
            </html>
            """, name, verificationUrl, verificationCode);
    
        sendHtmlEmail(to, subject, htmlContent);
    }

    public void sendPasswordResetEmail(String to, String resetUrl) throws MailException, MessagingException {
        String subject = "Password Reset Request";
        String htmlContent = String.format("""
            <!DOCTYPE html>
            <html>
            <head>
                <meta charset="UTF-8">
                <meta name="viewport" content="width=device-width, initial-scale=1.0">
                <style>
                    body { 
                        font-family: -apple-system, BlinkMacSystemFont, 'Segoe UI', Roboto, 'Helvetica Neue', Arial, sans-serif;
                        line-height: 1.6;
                        margin: 0;
                        padding: 0;
                        background-color: hsl(20 14.3%% 4.1%%);
                        color: hsl(60 9.1%% 97.8%%);
                    }
                    .container { 
                        max-width: 600px;
                        margin: 0 auto;
                        padding: 2rem;
                        background-color: hsl(20 14.3%% 4.1%%);
                    }
                    .email-wrapper {
                        background-color: hsl(12 6.5%% 15.1%%);
                        border-radius: 1rem;
                        overflow: hidden;
                        box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1), 0 2px 4px -1px rgba(0, 0, 0, 0.06);
                    }
                    .header { 
                        background-color: hsl(60 9.1%% 97.8%%);
                        color: hsl(20 14.3%% 4.1%%);
                        padding: 2.5rem 2rem;
                        text-align: center;
                    }
                    .content { 
                        padding: 2.5rem 2rem;
                    }
                    .button { 
                        display: inline-block;
                        padding: 1rem 2rem;
                        background-color: hsl(60 9.1%% 97.8%%);
                        color: hsl(20 14.3%% 4.1%%);
                        text-decoration: none;
                        border-radius: 0.75rem;
                        font-weight: 600;
                        font-size: 1.125rem;
                        margin: 1.5rem 0;
                        text-align: center;
                        min-width: 240px;
                        box-shadow: 0 4px 6px -1px rgba(0, 0, 0, 0.1), 0 2px 4px -1px rgba(0, 0, 0, 0.06);
                    }
                    .footer { 
                        text-align: center;
                        padding: 2rem;
                        color: hsl(24 5.4%% 63.9%%);
                        font-size: 0.875rem;
                        background-color: hsl(12 6.5%% 15.1%%);
                        border-top: 1px solid hsl(12 6.5%% 20.1%%);
                    }
                    h1 {
                        margin: 0;
                        font-size: 1.75rem;
                        font-weight: 700;
                        letter-spacing: -0.025em;
                    }
                    p {
                        margin: 1.25rem 0;
                        color: hsl(60 9.1%% 97.8%%);
                        font-size: 1.125rem;
                    }
                    .logo {
                        font-size: 2rem;
                        font-weight: 800;
                        margin-bottom: 1.5rem;
                        letter-spacing: -0.05em;
                    }
                    .divider {
                        height: 1px;
                        background-color: hsl(12 6.5%% 20.1%%);
                        margin: 2rem 0;
                    }
                    table {
                        width: 100%%;
                        background-color: hsl(20 14.3%% 4.1%%);
                    }
                    td {
                        padding: 0;
                    }
                    .button-wrapper {
                        text-align: center;
                        margin: 2rem 0;
                    }
                    .expiry-notice {
                        font-size: 0.875rem;
                        color: hsl(24 5.4%% 63.9%%);
                        margin-top: 1rem;
                    }
                </style>
            </head>
            <body>
                <table role="presentation" cellspacing="0" cellpadding="0" border="0">
                    <tr>
                        <td align="center">
                            <div class="container">
                                <div class="email-wrapper">
                                    <div class="header">
                                        <div class="logo">AuthKit</div>
                                        <h1>Password Reset Request</h1>
                                    </div>
                                    <div class="content">
                                        <p>We received a request to reset your password. Click the button below to create a new password:</p>
                                        
                                        <div class="button-wrapper">
                                            <a href="%s" class="button">Reset Password</a>
                                        </div>

                                        <p class="expiry-notice">This link will expire in 1 hour.</p>
                                        <p>If you did not request a password reset, please ignore this email or contact support if you have concerns.</p>
                                    </div>
                                    <div class="footer">
                                        <p>This is an automated message, please do not reply to this email.</p>
                                    </div>
                                </div>
                            </div>
                        </td>
                    </tr>
                </table>
            </body>
            </html>
            """, resetUrl);
    
        sendHtmlEmail(to, subject, htmlContent);
    }
}
