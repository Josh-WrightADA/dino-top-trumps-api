package com.dinotoptrumps.auth.ports.in;

import com.dinotoptrumps.auth.domain.model.User;

import java.util.List;
import java.util.UUID;

public interface ForAdminOperations {
    List<User> getAllUsers();
    User banUser(UUID userId);
    User unbanUser(UUID userId);
}
