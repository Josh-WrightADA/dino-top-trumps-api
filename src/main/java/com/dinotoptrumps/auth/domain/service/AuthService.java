package com.dinotoptrumps.auth.domain.service;

import com.dinotoptrumps.auth.domain.exception.InvalidCredentialsException;
import com.dinotoptrumps.auth.domain.exception.InvalidPasswordException;
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
    private final ProfanityFilter profanityFilter;

    public AuthService(ForPersistingUsers userRepository, ForEncodingPasswords passwordEncoder,
                       ProfanityFilter profanityFilter) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.profanityFilter = profanityFilter;
    }

    @Override
    public User register(String username, String email, String rawPassword) {
        profanityFilter.validate(username, "Username");

        if (userRepository.findByUsername(username).isPresent()) {
            throw new UserAlreadyExistsException("Username already taken: " + username);
        }
        if (userRepository.findByEmail(email).isPresent()) {
            throw new UserAlreadyExistsException("Email already registered: " + email);
        }

        String hashedPassword = passwordEncoder.encode(rawPassword);
        User user = User.create(username, email, hashedPassword);
        User saved = userRepository.save(user);
        log.info("event_type=AUTH_REGISTER_SUCCESS username={}", username);
        return saved;
    }

    @Override
    public User authenticate(String username, String rawPassword) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new InvalidCredentialsException("Invalid username or password"));

        if (!passwordEncoder.matches(rawPassword, user.getPasswordHash())) {
            log.warn("event_type=AUTH_LOGIN_FAILED username={}", username);
            throw new InvalidCredentialsException("Invalid username or password");
        }

        if (user.isBanned()) {
            log.warn("event_type=AUTH_LOGIN_BANNED username={}", username);
            throw new InvalidCredentialsException("Account has been banned");
        }

        log.info("event_type=AUTH_LOGIN_SUCCESS username={}", username);
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
    public User updateProfile(UUID userId, String displayName, String bio, UUID favouriteCardId) {
        if (displayName != null) {
            profanityFilter.validate(displayName, "Display name");
        }
        if (bio != null) {
            profanityFilter.validate(bio, "Bio");
        }

        User user = userRepository.findById(userId)
                .orElseThrow(() -> new InvalidCredentialsException("User not found"));
        if (displayName != null) {
            user.setDisplayName(displayName);
        }
        user.setBio(bio);
        user.setFavouriteCardId(favouriteCardId);
        log.info("event_type=PROFILE_UPDATED userId={}", userId);
        return userRepository.save(user);
    }

    @Override
    public User updateAvatar(UUID userId, String avatarUrl) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new InvalidCredentialsException("User not found"));
        user.setAvatarUrl(avatarUrl);
        User saved = userRepository.save(user);
        log.info("event_type=AVATAR_CHANGED userId={}", userId);
        return saved;
    }

    @Override
    public void changePassword(UUID userId, String currentPassword, String newPassword) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new InvalidCredentialsException("User not found"));
        if (!passwordEncoder.matches(currentPassword, user.getPasswordHash())) {
            throw new InvalidPasswordException("Current password is incorrect");
        }
        user.setPasswordHash(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        log.info("event_type=PASSWORD_CHANGED userId={}", userId);
    }

    @Override
    public void deleteAccount(UUID userId) {
        userRepository.findById(userId)
                .orElseThrow(() -> new InvalidCredentialsException("User not found"));
        userRepository.deleteById(userId);
        log.info("event_type=ACCOUNT_DELETED userId={}", userId);
    }

    @Override
    public UserProfile getPublicProfile(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new InvalidCredentialsException("User not found"));
        return UserProfile.fromUser(user);
    }
}
