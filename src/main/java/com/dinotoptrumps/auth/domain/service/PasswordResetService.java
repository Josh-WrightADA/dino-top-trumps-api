package com.dinotoptrumps.auth.domain.service;

import com.dinotoptrumps.auth.domain.model.PasswordResetToken;
import com.dinotoptrumps.auth.domain.model.User;
import com.dinotoptrumps.auth.ports.in.ForResettingPassword;
import com.dinotoptrumps.auth.ports.out.ForPersistingResetTokens;
import com.dinotoptrumps.auth.ports.out.ForPersistingUsers;
import com.dinotoptrumps.auth.ports.out.ForSendingEmails;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;

public class PasswordResetService implements ForResettingPassword {

    private final ForPersistingUsers userRepository;
    private final ForPersistingResetTokens tokenRepository;
    private final ForSendingEmails emailSender;

    public PasswordResetService(ForPersistingUsers userRepository,
                                ForPersistingResetTokens tokenRepository,
                                ForSendingEmails emailSender) {
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
        this.emailSender = emailSender;
    }

    @Override
    public void requestPasswordReset(String email) {
        Optional<User> userOpt = userRepository.findByEmail(email);
        if (userOpt.isEmpty()) {
            // Silently return to prevent email enumeration
            return;
        }

        User user = userOpt.get();
        String token = UUID.randomUUID().toString();
        Instant expiresAt = Instant.now().plus(1, ChronoUnit.HOURS);

        PasswordResetToken resetToken = PasswordResetToken.create(user.getId(), token, expiresAt);
        tokenRepository.save(resetToken);
        emailSender.sendPasswordResetEmail(user.getEmail(), token);
    }

    @Override
    public void resetPassword(String token, String newRawPassword) {
        PasswordResetToken resetToken = tokenRepository.findByToken(token)
                .orElseThrow(() -> new IllegalArgumentException("Invalid reset token"));

        if (!resetToken.isValid()) {
            throw new IllegalArgumentException("Reset token is expired or already used");
        }

        User user = userRepository.findById(resetToken.getUserId())
                .orElseThrow(() -> new IllegalStateException("User not found for reset token"));

        // TODO: Hash the new password
        user.setPasswordHash(newRawPassword);
        userRepository.save(user);

        resetToken.markUsed();
        tokenRepository.save(resetToken);
    }
}
