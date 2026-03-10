package com.dinotoptrumps.auth.adapters.out.email;

import com.dinotoptrumps.auth.ports.out.ForSendingEmails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class EmailAdapter implements ForSendingEmails {

    private static final Logger log = LoggerFactory.getLogger(EmailAdapter.class);

    @Override
    public void sendPasswordResetEmail(String toEmail, String resetToken) {
        // TODO: Integrate with SendGrid for production email delivery
        log.info("Password reset requested for email={}, token={}", toEmail, resetToken);
    }
}
