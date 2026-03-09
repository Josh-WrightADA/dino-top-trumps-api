package com.dinotoptrumps.auth.ports.in;

import com.dinotoptrumps.auth.domain.model.User;

public interface ForRegistering {
    User register(String username, String email, String rawPassword);
}
