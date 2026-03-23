package com.dinotoptrumps.game.adapters.in;

import com.dinotoptrumps.auth.domain.model.RankTier;
import com.dinotoptrumps.game.adapters.in.dto.LeaderboardEntry;
import com.dinotoptrumps.auth.ports.in.ForGettingLeaderboard;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/leaderboard")
public class LeaderboardController {

    private static final int DEFAULT_LEADERBOARD_SIZE = 20;

    private final ForGettingLeaderboard forGettingLeaderboard;

    public LeaderboardController(ForGettingLeaderboard forGettingLeaderboard) {
        this.forGettingLeaderboard = forGettingLeaderboard;
    }

    @GetMapping
    public ResponseEntity<List<LeaderboardEntry>> getLeaderboard() {
        List<LeaderboardEntry> entries = forGettingLeaderboard.getTopPlayers(DEFAULT_LEADERBOARD_SIZE)
                .stream()
                .map(profile -> new LeaderboardEntry(
                        profile.getUserId(),
                        profile.getUsername(),
                        profile.getDisplayName(),
                        RankTier.calculateLeaguePoints(profile.getEloRating()),
                        profile.getGamesPlayed(),
                        profile.getGamesWon(),
                        RankTier.fromElo(profile.getEloRating())
                ))
                .toList();
        return ResponseEntity.ok(entries);
    }
}
