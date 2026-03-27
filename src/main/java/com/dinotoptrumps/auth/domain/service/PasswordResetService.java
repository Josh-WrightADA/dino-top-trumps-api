package com.dinotoptrumps.auth.domain.service;

import com.dinotoptrumps.auth.domain.model.PasswordResetToken;
import com.dinotoptrumps.auth.domain.model.User;
import com.dinotoptrumps.auth.ports.in.ForResettingPassword;
import com.dinotoptrumps.auth.ports.out.ForEncodingPasswords;
import com.dinotoptrumps.auth.ports.out.ForPersistingResetTokens;
import com.dinotoptrumps.auth.ports.out.ForPersistingUsers;
import com.dinotoptrumps.auth.ports.out.ForSendingEmails;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;

public class PasswordResetService implements ForResettingPassword {

    private static final Logger log = LoggerFactory.getLogger(PasswordResetService.class);

    private final ForPersistingUsers userRepository;
    private final ForPersistingResetTokens tokenRepository;
    private final ForSendingEmails emailSender;
    private final ForEncodingPasswords passwordEncoder;

    public PasswordResetService(ForPersistingUsers userRepository,
                                ForPersistingResetTokens tokenRepository,
                                ForSendingEmails emailSender,
                                ForEncodingPasswords passwordEncoder) {
        this.userRepository = userRepository;
        this.tokenRepository = tokenRepository;
        this.emailSender = emailSender;
        this.passwordEncoder = passwordEncoder;
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
        log.info("event_type=PASSWORD_RESET_REQUESTED userId={}", user.getId());
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

        String hashedPassword = passwordEncoder.encode(newRawPassword);
        user.resetPasswordTo(hashedPassword);
        userRepository.save(user);

        resetToken.markUsed();
        tokenRepository.save(resetToken);
        log.info("event_type=PASSWORD_RESET_COMPLETED userId={}", user.getId());
    }
}
