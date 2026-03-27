package com.dinotoptrumps.auth.domain.service;

import com.dinotoptrumps.auth.domain.exception.UserNotFoundException;
import com.dinotoptrumps.auth.domain.model.User;
import com.dinotoptrumps.auth.ports.in.ForAdminOperations;
import com.dinotoptrumps.auth.ports.out.ForPersistingUsers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.UUID;

public class AdminService implements ForAdminOperations {

    private static final Logger log = LoggerFactory.getLogger(AdminService.class);

    private final ForPersistingUsers userRepository;

    public AdminService(ForPersistingUsers userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public User banUser(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        if (user.isAdmin()) {
            throw new IllegalStateException("Cannot ban an admin user");
        }
        user.ban();
        User saved = userRepository.save(user);
        log.info("event_type=USER_BANNED userId={}", userId);
        return saved;
    }

    @Override
    public User unbanUser(UUID userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new UserNotFoundException("User not found"));
        user.unban();
        User saved = userRepository.save(user);
        log.info("event_type=USER_UNBANNED userId={}", userId);
        return saved;
    }
}
