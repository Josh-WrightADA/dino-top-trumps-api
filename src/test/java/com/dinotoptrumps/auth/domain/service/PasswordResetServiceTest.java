package com.dinotoptrumps.auth.domain.service;

import com.dinotoptrumps.auth.domain.model.PasswordResetToken;
import com.dinotoptrumps.auth.domain.model.User;
import com.dinotoptrumps.auth.ports.out.ForEncodingPasswords;
import com.dinotoptrumps.auth.ports.out.ForPersistingResetTokens;
import com.dinotoptrumps.auth.ports.out.ForPersistingUsers;
import com.dinotoptrumps.auth.ports.out.ForSendingEmails;
import com.dinotoptrumps.support.TestFixtures;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class PasswordResetServiceTest {

    @Mock
    private ForPersistingUsers userRepository;

    @Mock
    private ForPersistingResetTokens tokenRepository;

    @Mock
    private ForSendingEmails emailSender;

    @Mock
    private ForEncodingPasswords passwordEncoder;

    private PasswordResetService passwordResetService;

    @BeforeEach
    void setUp() {
        passwordResetService = new PasswordResetService(
                userRepository, tokenRepository, emailSender, passwordEncoder);
    }

    @Nested
    class RequestPasswordReset {

        @Test
        void requestReset_existingEmail_sendsEmail() {
            User user = TestFixtures.createUser(UUID.randomUUID(), "alice");
            when(userRepository.findByEmail("alice@test.com")).thenReturn(Optional.of(user));
            when(tokenRepository.save(any(PasswordResetToken.class))).thenAnswer(inv -> inv.getArgument(0));

            passwordResetService.requestPasswordReset("alice@test.com");

            verify(emailSender).sendPasswordResetEmail(eq("alice@test.com"), anyString());
            verify(tokenRepository).save(any(PasswordResetToken.class));
        }

        @Test
        void requestReset_nonExistentEmail_silentlyReturns() {
            when(userRepository.findByEmail("ghost@test.com")).thenReturn(Optional.empty());

            passwordResetService.requestPasswordReset("ghost@test.com");

            verify(emailSender, never()).sendPasswordResetEmail(anyString(), anyString());
            verify(tokenRepository, never()).save(any());
        }
    }

    @Nested
    class ResetPassword {

        @Test
        void resetPassword_validToken_updatesPassword() {
            UUID userId = UUID.randomUUID();
            User user = TestFixtures.createUser(userId, "alice");
            Instant futureExpiry = Instant.now().plus(1, ChronoUnit.HOURS);
            PasswordResetToken token = PasswordResetToken.create(userId, "valid-token", futureExpiry);

            when(tokenRepository.findByToken("valid-token")).thenReturn(Optional.of(token));
            when(userRepository.findById(userId)).thenReturn(Optional.of(user));
            when(passwordEncoder.encode("newpassword")).thenReturn("newhash");
            when(tokenRepository.save(any(PasswordResetToken.class))).thenAnswer(inv -> inv.getArgument(0));
            when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

            passwordResetService.resetPassword("valid-token", "newpassword");

            verify(userRepository).save(any(User.class));
            verify(tokenRepository).save(any(PasswordResetToken.class));
        }

        @Test
        void resetPassword_invalidToken_throws() {
            when(tokenRepository.findByToken("bad-token")).thenReturn(Optional.empty());

            assertThrows(IllegalArgumentException.class,
                    () -> passwordResetService.resetPassword("bad-token", "newpassword"));
        }

        @Test
        void resetPassword_expiredToken_throws() {
            UUID userId = UUID.randomUUID();
            Instant pastExpiry = Instant.now().minus(1, ChronoUnit.HOURS);
            PasswordResetToken expired = new PasswordResetToken(
                    UUID.randomUUID(), userId, "expired-token", pastExpiry, false, Instant.now());

            when(tokenRepository.findByToken("expired-token")).thenReturn(Optional.of(expired));

            assertThrows(IllegalArgumentException.class,
                    () -> passwordResetService.resetPassword("expired-token", "newpassword"));
        }

        @Test
        void resetPassword_usedToken_throws() {
            UUID userId = UUID.randomUUID();
            Instant futureExpiry = Instant.now().plus(1, ChronoUnit.HOURS);
            PasswordResetToken used = new PasswordResetToken(
                    UUID.randomUUID(), userId, "used-token", futureExpiry, true, Instant.now());

            when(tokenRepository.findByToken("used-token")).thenReturn(Optional.of(used));

            assertThrows(IllegalArgumentException.class,
                    () -> passwordResetService.resetPassword("used-token", "newpassword"));
        }
    }
}
