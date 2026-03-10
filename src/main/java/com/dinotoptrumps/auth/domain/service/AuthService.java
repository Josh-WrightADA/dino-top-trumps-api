package com.dinotoptrumps.auth.domain.service;

import com.dinotoptrumps.auth.domain.model.User;
import com.dinotoptrumps.auth.ports.in.ForAuthenticating;
import com.dinotoptrumps.auth.ports.in.ForManagingProfile;
import com.dinotoptrumps.auth.ports.in.ForRegistering;
import com.dinotoptrumps.auth.ports.out.ForEncodingPasswords;
import com.dinotoptrumps.auth.ports.out.ForPersistingUsers;
import com.dinotoptrumps.auth.domain.exception.UserAlreadyExistsException;
import com.dinotoptrumps.auth.domain.exception.InvalidCredentialsException;

import java.util.Optional;
import java.util.UUID;

public class AuthService implements ForRegistering, ForAuthenticating, ForManagingProfile {

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
        return userRepository.save(user);
    }

    @Override
    public User authenticate(String username, String rawPassword) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new InvalidCredentialsException("Invalid username or password"));

        if (!passwordEncoder.matches(rawPassword, user.getPasswordHash())) {
            throw new InvalidCredentialsException("Invalid username or password");
        }
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
}
