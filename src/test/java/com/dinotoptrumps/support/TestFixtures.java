package com.dinotoptrumps.support;

import com.dinotoptrumps.auth.domain.model.AccountStatus;
import com.dinotoptrumps.auth.domain.model.Role;
import com.dinotoptrumps.auth.domain.model.User;
import com.dinotoptrumps.game.domain.model.Card;

import java.time.Instant;
import java.util.UUID;

public class TestFixtures {

    private TestFixtures() {
    }

    public static User createUser(UUID id, String username) {
        return createUser(id, username, Role.PLAYER, AccountStatus.ACTIVE);
    }

    public static User createUser(UUID id, String username, Role role, AccountStatus status) {
        Instant now = Instant.now();
        return new User(id, username, username + "@test.com", "hash",
                username, "", null, null, role, status,
                1000, 0, 0, now, now);
    }

    public static Card createCard(UUID id, String name, int height, int weight,
                                   int intelligence, int speed, int strength) {
        return new Card(id, name, "meaning", "Carnivore", "Cretaceous",
                null, "", null, height, weight, intelligence, speed, strength);
    }
}
