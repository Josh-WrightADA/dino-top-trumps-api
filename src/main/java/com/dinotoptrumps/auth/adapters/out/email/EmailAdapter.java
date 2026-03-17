package com.dinotoptrumps.auth.adapters.out.email;

import com.dinotoptrumps.auth.ports.out.ForSendingEmails;
import com.sendgrid.Method;
import com.sendgrid.Request;
import com.sendgrid.Response;
import com.sendgrid.SendGrid;
import com.sendgrid.helpers.mail.Mail;
import com.sendgrid.helpers.mail.objects.Content;
import com.sendgrid.helpers.mail.objects.Email;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class EmailAdapter implements ForSendingEmails {

    private static final Logger log = LoggerFactory.getLogger(EmailAdapter.class);

    private final String apiKey;
    private final String fromEmail;
    private final String frontendUrl;

    public EmailAdapter(
            @Value("${sendgrid.api-key}") String apiKey,
            @Value("${sendgrid.from-email}") String fromEmail,
            @Value("${app.frontend-url}") String frontendUrl) {
        this.apiKey = apiKey;
        this.fromEmail = fromEmail;
        this.frontendUrl = frontendUrl;
    }

    @Override
    public void sendPasswordResetEmail(String toEmail, String resetToken) {
        if ("disabled".equals(apiKey)) {
            log.info("SendGrid disabled — reset link: {}/reset-password?token={}",
                    frontendUrl, resetToken);
            return;
        }

        String resetLink = frontendUrl + "/reset-password?token=" + resetToken;

        Email from = new Email(fromEmail, "Dino Top Trumps");
        Email to = new Email(toEmail);
        String subject = "Reset your Dino Top Trumps password";
        Content content = new Content("text/html", buildResetEmailHtml(resetLink));
        Mail mail = new Mail(from, subject, to, content);

        SendGrid sg = new SendGrid(apiKey);
        Request request = new Request();

        try {
            request.setMethod(Method.POST);
            request.setEndpoint("mail/send");
            request.setBody(mail.build());
            Response response = sg.api(request);

            if (response.getStatusCode() >= 200 && response.getStatusCode() < 300) {
                log.info("Password reset email sent to {}", toEmail);
            } else {
                log.error("SendGrid returned status {}: {}",
                        response.getStatusCode(), response.getBody());
            }
        } catch (IOException e) {
            log.error("Failed to send password reset email to {}", toEmail, e);
        }
    }

    private String buildResetEmailHtml(String resetLink) {
        return "<div style=\"font-family: sans-serif; max-width: 600px; margin: 0 auto;\">"
                + "<h2 style=\"color: #2d6a4f;\">Dino Top Trumps</h2>"
                + "<p>You requested a password reset. Click the link below to set a new password:</p>"
                + "<p><a href=\"" + resetLink + "\" style=\"display: inline-block; padding: 12px 24px; "
                + "background: #2d6a4f; color: #fff; text-decoration: none; border-radius: 4px;\">"
                + "Reset Password</a></p>"
                + "<p style=\"color: #888; font-size: 0.85em;\">This link expires in 1 hour. "
                + "If you did not request this, you can safely ignore this email.</p>"
                + "</div>";
    }
}
