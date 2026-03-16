package com.dinotoptrumps.auth.ports.in;

import com.dinotoptrumps.auth.domain.model.UserProfile;

import java.util.List;

public interface ForGettingLeaderboard {
    List<UserProfile> getTopPlayers(int limit);
}
