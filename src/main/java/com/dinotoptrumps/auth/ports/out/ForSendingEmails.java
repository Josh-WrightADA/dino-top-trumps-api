package com.dinotoptrumps.auth.ports.out;

public interface ForSendingEmails {
    void sendPasswordResetEmail(String toEmail, String resetToken);
}
