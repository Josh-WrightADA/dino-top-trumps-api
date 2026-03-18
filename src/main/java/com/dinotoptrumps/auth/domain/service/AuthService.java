package com.dinotoptrumps.auth.domain.service;

import com.dinotoptrumps.auth.domain.exception.InvalidCredentialsException;
import com.dinotoptrumps.auth.domain.exception.UserAlreadyExistsException;
import com.dinotoptrumps.auth.domain.model.User;
import com.dinotoptrumps.auth.domain.model.UserProfile;
import com.dinotoptrumps.auth.ports.in.ForAuthenticating;
import com.dinotoptrumps.auth.ports.in.ForManagingProfile;
import com.dinotoptrumps.auth.ports.in.ForRegistering;
import com.dinotoptrumps.auth.ports.in.ForViewingPublicProfile;
import com.dinotoptrumps.auth.ports.out.ForEncodingPasswords;
import com.dinotoptrumps.auth.ports.out.ForPersistingUsers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.UUID;

public class AuthService implements ForRegistering, ForAuthenticating, ForManagingProfile, ForViewingPublicProfile {

    private static final Logger log = LoggerFactory.getLogger(AuthService.class);

    private final ForPersistingUsers userRepository;
    private final ForEncodingPasswords passwordEncoder;

    public AuthService(ForPersistingUsers userRepository, ForEncodingPasswords passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public User register(String username, String email, String rawPassword) {
        if (userRepository.findByUsername(username).isPresent()) {
            throw new UserAlreadyExistsException("Username already taken: " + username);
        }
        if (userRepository.findByEmail(email).isPresent()) {
            throw new UserAlreadyExistsException("Email already registered: " + email);
        }

        String hashedPassword = passwordEncoder.encode(rawPassword);
        User user = User.create(username, email, hashedPassword);
        User saved = userRepository.save(user);
        log.info("User registered: {}", username);
        return saved;
    }

    @Override
    public User authenticate(String username, String rawPassword) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new InvalidCredentialsException("Invalid username or password"));

        if (!passwordEncoder.matches(rawPassword, user.getPasswordHash())) {
            throw new InvalidCredentialsException("Invalid username or password");
        }
        log.info("User authenticated: {}", username);
        return user;
    }

    @Override
    public User getProfile(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new InvalidCredentialsException("User not found"));
    }

    @Override
    public User updateDisplayName(UUID userId, String displayName) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new InvalidCredentialsException("User not found"));
        user.setDisplayName(displayName);
        return userRepository.save(user);
    }

    @Override
    public void changePassword(UUID userId, String currentPassword, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new InvalidCredentialsException("User not found"));
        if (!passwordEncoder.matches(currentPassword, user.getPasswordHash())) {
            throw new InvalidCredentialsException("Current password is incorrect");
        }
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        log.info("Password changed for user: {}", userId);
    }

    @Override
    public void deleteAccount(UUID userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new InvalidCredentialsException("User not found"));
        userRepository.deleteById(userId);
        log.info("Account deleted for user: {}", userId);
    }

    @Override
    public UserProfile getPublicProfile(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new InvalidCredentialsException("User not found"));
        return UserProfile.fromUser(user);
    }
}
