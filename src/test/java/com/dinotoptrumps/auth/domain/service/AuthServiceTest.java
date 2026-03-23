package com.dinotoptrumps.auth.domain.service;

import com.dinotoptrumps.auth.domain.exception.InvalidCredentialsException;
import com.dinotoptrumps.auth.domain.exception.InvalidPasswordException;
import com.dinotoptrumps.auth.domain.exception.UserAlreadyExistsException;
import com.dinotoptrumps.auth.domain.model.AccountStatus;
import com.dinotoptrumps.auth.domain.model.Role;
import com.dinotoptrumps.auth.domain.model.User;
import com.dinotoptrumps.auth.ports.out.ForEncodingPasswords;
import com.dinotoptrumps.auth.ports.out.ForPersistingUsers;
import com.dinotoptrumps.support.TestFixtures;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private ForPersistingUsers userRepository;

    @Mock
    private ForEncodingPasswords passwordEncoder;

    @Mock
    private ProfanityFilter profanityFilter;

    private AuthService authService;

    @BeforeEach
    void setUp() {
        authService = new AuthService(userRepository, passwordEncoder, profanityFilter);
    }

    @Nested
    class Register {

        @Test
        void register_success_savesUserWithCorrectFields() {
            when(userRepository.findByUsername("newuser")).thenReturn(Optional.empty());
            when(userRepository.findByEmail("new@test.com")).thenReturn(Optional.empty());
            when(passwordEncoder.encode("password123")).thenReturn("hashedpw");
            when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

            User result = authService.register("newuser", "new@test.com", "password123");

            assertEquals("newuser", result.getUsername());
            assertEquals("new@test.com", result.getEmail());
            assertEquals("hashedpw", result.getPasswordHash());
            verify(userRepository).save(any(User.class));
        }

        @Test
        void register_duplicateUsername_throws() {
            User existing = TestFixtures.createUser(UUID.randomUUID(), "taken");
            when(userRepository.findByUsername("taken")).thenReturn(Optional.of(existing));

            assertThrows(UserAlreadyExistsException.class,
                    () -> authService.register("taken", "unique@test.com", "password123"));

            verify(userRepository, never()).save(any());
        }

        @Test
        void register_duplicateEmail_throws() {
            when(userRepository.findByUsername("uniqueuser")).thenReturn(Optional.empty());
            User existing = TestFixtures.createUser(UUID.randomUUID(), "other");
            when(userRepository.findByEmail("taken@test.com")).thenReturn(Optional.of(existing));

            assertThrows(UserAlreadyExistsException.class,
                    () -> authService.register("uniqueuser", "taken@test.com", "password123"));

            verify(userRepository, never()).save(any());
        }

        @Test
        void register_profanityInUsername_throws() {
            doThrow(new IllegalArgumentException("Username contains profanity"))
                    .when(profanityFilter).validate(anyString(), anyString());

            assertThrows(IllegalArgumentException.class,
                    () -> authService.register("badword", "clean@test.com", "password123"));

            verify(userRepository, never()).save(any());
        }
    }

    @Nested
    class Authenticate {

        @Test
        void authenticate_success_returnsUser() {
            User user = TestFixtures.createUser(UUID.randomUUID(), "alice");
            when(userRepository.findByUsername("alice")).thenReturn(Optional.of(user));
            when(passwordEncoder.matches("password123", "hash")).thenReturn(true);

            User result = authService.authenticate("alice", "password123");

            assertEquals("alice", result.getUsername());
        }

        @Test
        void authenticate_wrongPassword_throws() {
            User user = TestFixtures.createUser(UUID.randomUUID(), "alice");
            when(userRepository.findByUsername("alice")).thenReturn(Optional.of(user));
            when(passwordEncoder.matches("wrongpw", "hash")).thenReturn(false);

            assertThrows(InvalidCredentialsException.class,
                    () -> authService.authenticate("alice", "wrongpw"));
        }

        @Test
        void authenticate_bannedUser_throwsWithBannedMessage() {
            User banned = TestFixtures.createUser(UUID.randomUUID(), "banned", Role.PLAYER, AccountStatus.BANNED);
            when(userRepository.findByUsername("banned")).thenReturn(Optional.of(banned));
            when(passwordEncoder.matches("password123", "hash")).thenReturn(true);

            InvalidCredentialsException ex = assertThrows(InvalidCredentialsException.class,
                    () -> authService.authenticate("banned", "password123"));

            assertEquals("Account has been banned", ex.getMessage());
        }

        @Test
        void authenticate_nonExistentUser_throws() {
            when(userRepository.findByUsername("ghost")).thenReturn(Optional.empty());

            assertThrows(InvalidCredentialsException.class,
                    () -> authService.authenticate("ghost", "password123"));
        }
    }

    @Nested
    class UpdateProfile {

        @Test
        void updateProfile_success_updatesDisplayNameAndBio() {
            UUID userId = UUID.randomUUID();
            User user = TestFixtures.createUser(userId, "alice");
            when(userRepository.findById(userId)).thenReturn(Optional.of(user));
            when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

            User result = authService.updateProfile(userId, "Alice Smith", "I love dinos", null);

            assertEquals("Alice Smith", result.getDisplayName());
            assertEquals("I love dinos", result.getBio());
        }

        @Test
        void updateProfile_profanityInDisplayName_throws() {
            UUID userId = UUID.randomUUID();
            doThrow(new IllegalArgumentException("Display name contains profanity"))
                    .when(profanityFilter).validate(anyString(), anyString());

            assertThrows(IllegalArgumentException.class,
                    () -> authService.updateProfile(userId, "badword", null, null));
        }

        @Test
        void updateProfile_profanityInBio_throws() {
            UUID userId = UUID.randomUUID();
            doThrow(new IllegalArgumentException("Bio contains profanity"))
                    .when(profanityFilter).validate(anyString(), anyString());

            assertThrows(IllegalArgumentException.class,
                    () -> authService.updateProfile(userId, null, "offensive bio", null));
        }
    }

    @Nested
    class ChangePassword {

        @Test
        void changePassword_success() {
            UUID userId = UUID.randomUUID();
            User user = TestFixtures.createUser(userId, "alice");
            when(userRepository.findById(userId)).thenReturn(Optional.of(user));
            when(passwordEncoder.matches("password123", "hash")).thenReturn(true);
            when(passwordEncoder.encode("newpassword456")).thenReturn("newhash");

            authService.changePassword(userId, "password123", "newpassword456");

            verify(userRepository).save(any(User.class));
        }

        @Test
        void changePassword_wrongCurrent_throws() {
            UUID userId = UUID.randomUUID();
            User user = TestFixtures.createUser(userId, "alice");
            when(userRepository.findById(userId)).thenReturn(Optional.of(user));
            when(passwordEncoder.matches("wrongpw", "hash")).thenReturn(false);

            assertThrows(InvalidPasswordException.class,
                    () -> authService.changePassword(userId, "wrongpw", "newpassword456"));

            verify(userRepository, never()).save(any());
        }

        @Test
        void changePassword_samePassword_throws() {
            UUID userId = UUID.randomUUID();
            User user = TestFixtures.createUser(userId, "alice");
            when(userRepository.findById(userId)).thenReturn(Optional.of(user));
            when(passwordEncoder.matches("password123", "hash")).thenReturn(true);

            assertThrows(InvalidPasswordException.class,
                    () -> authService.changePassword(userId, "password123", "password123"));

            verify(userRepository, never()).save(any());
        }
    }

    @Nested
    class DeleteAccount {

        @Test
        void deleteAccount_correctPassword_deletes() {
            UUID userId = UUID.randomUUID();
            User user = TestFixtures.createUser(userId, "alice");
            when(userRepository.findById(userId)).thenReturn(Optional.of(user));
            when(passwordEncoder.matches("password123", "hash")).thenReturn(true);

            authService.deleteAccount(userId, "password123");

            verify(userRepository).deleteById(userId);
        }

        @Test
        void deleteAccount_wrongPassword_throws() {
            UUID userId = UUID.randomUUID();
            User user = TestFixtures.createUser(userId, "alice");
            when(userRepository.findById(userId)).thenReturn(Optional.of(user));
            when(passwordEncoder.matches("wrongpw", "hash")).thenReturn(false);

            assertThrows(InvalidPasswordException.class,
                    () -> authService.deleteAccount(userId, "wrongpw"));

            verify(userRepository, never()).deleteById(any());
        }
    }
}
