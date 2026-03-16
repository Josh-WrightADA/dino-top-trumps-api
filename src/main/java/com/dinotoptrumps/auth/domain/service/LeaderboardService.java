package com.dinotoptrumps.auth.domain.service;

import com.dinotoptrumps.auth.domain.model.UserProfile;
import com.dinotoptrumps.auth.ports.in.ForGettingLeaderboard;
import com.dinotoptrumps.auth.ports.out.ForPersistingUsers;

import java.util.List;

public class LeaderboardService implements ForGettingLeaderboard {

    private final ForPersistingUsers userRepository;

    public LeaderboardService(ForPersistingUsers userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public List<UserProfile> getTopPlayers(int limit) {
        return userRepository.findTopByEloRating(limit).stream()
                .map(UserProfile::fromUser)
                .toList();
    }
}
