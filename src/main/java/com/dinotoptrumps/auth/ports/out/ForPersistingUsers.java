package com.dinotoptrumps.auth.ports.out;

import com.dinotoptrumps.auth.domain.model.User;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ForPersistingUsers {
    User save(User user);
    Optional<User> findByUsername(String username);
    Optional<User> findByEmail(String email);
    Optional<User> findById(UUID id);
    List<User> findAll();
    List<User> findTopByEloRating(int limit);
    void deleteById(UUID id);
}
