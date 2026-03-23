package com.dinotoptrumps.social.ports.out;

import java.util.UUID;

public interface ForLookingUpUsers {
    boolean userExistsAndIsActive(UUID userId);
}
