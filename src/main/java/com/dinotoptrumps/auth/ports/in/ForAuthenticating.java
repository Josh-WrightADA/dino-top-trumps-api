package com.dinotoptrumps.auth.ports.in;

import com.dinotoptrumps.auth.domain.model.User;

public interface ForAuthenticating {
    User authenticate(String username, String rawPassword);
}
