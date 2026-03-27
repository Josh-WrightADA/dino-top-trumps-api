package com.dinotoptrumps.social.ports.out;

import java.util.Optional;
import java.util.UUID;

public interface ForLookingUpUsers {
    boolean userExistsAndIsActive(UUID userId);
    Optional<String> findDisplayNameById(UUID userId);
}
