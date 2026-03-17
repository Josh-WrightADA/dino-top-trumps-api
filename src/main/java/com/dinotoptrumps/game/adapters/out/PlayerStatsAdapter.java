package com.dinotoptrumps.game.adapters.out;

import com.dinotoptrumps.auth.domain.model.User;
import com.dinotoptrumps.auth.ports.out.ForPersistingUsers;
import com.dinotoptrumps.game.domain.service.EloService;
import com.dinotoptrumps.game.ports.out.ForUpdatingPlayerStats;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Component
public class PlayerStatsAdapter implements ForUpdatingPlayerStats {

    private final ForPersistingUsers userRepository;
    private final EloService eloService;

    public PlayerStatsAdapter(ForPersistingUsers userRepository, EloService eloService) {
        this.userRepository = userRepository;
        this.eloService = eloService;
    }

    @Override
    public void updateStatsAfterGame(UUID winnerId, UUID loserId) {
        User winner = userRepository.findById(winnerId)
                .orElseThrow(() -> new IllegalStateException("Winner not found: " + winnerId));
        User loser = userRepository.findById(loserId)
                .orElseThrow(() -> new IllegalStateException("Loser not found: " + loserId));

        int[] newRatings = eloService.updateRatings(winner.getEloRating(), loser.getEloRating());

        winner.setEloRating(newRatings[0]);
        winner.incrementGamesPlayed();
        winner.incrementGamesWon();

        loser.setEloRating(newRatings[1]);
        loser.incrementGamesPlayed();

        userRepository.save(winner);
        userRepository.save(loser);
    }
}
