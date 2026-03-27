package com.dinotoptrumps.social.adapters.out;

import com.dinotoptrumps.auth.ports.out.ForPersistingUsers;
import com.dinotoptrumps.social.ports.out.ForLookingUpUsers;
import org.springframework.stereotype.Component;

import java.util.Optional;
import java.util.UUID;

// This adapter bridges the social→auth bounded context boundary.
// It accesses auth domain models because it is the integration layer.
// In a microservices architecture, this would be an API call instead.
@Component
public class UserLookupAdapter implements ForLookingUpUsers {

    private final ForPersistingUsers userRepo;

    public UserLookupAdapter(ForPersistingUsers userRepo) {
        this.userRepo = userRepo;
    }

    @Override
    public boolean userExistsAndIsActive(UUID userId) {
        return userRepo.findById(userId)
                .map(user -> !user.isBanned())
                .orElse(false);
    }

    @Override
    public Optional<String> findDisplayNameById(UUID userId) {
        return userRepo.findById(userId)
                .map(user -> user.getDisplayName());
    }
}
