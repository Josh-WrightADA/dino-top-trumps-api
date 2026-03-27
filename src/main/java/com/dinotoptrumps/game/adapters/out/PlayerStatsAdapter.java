package com.dinotoptrumps.game.adapters.out;

import com.dinotoptrumps.auth.domain.model.User;
import com.dinotoptrumps.auth.ports.out.ForPersistingUsers;
import com.dinotoptrumps.game.domain.service.EloService;
import com.dinotoptrumps.game.ports.out.ForUpdatingPlayerStats;
import com.dinotoptrumps.shared.exception.DataIntegrityException;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

// This adapter bridges the game→auth bounded context boundary.
// It accesses User domain model because it needs to update player stats.
// In a microservices architecture, this would be an API call instead.
@Component
public class PlayerStatsAdapter implements ForUpdatingPlayerStats {

    private final ForPersistingUsers userRepository;
    private final EloService eloService;

    public PlayerStatsAdapter(ForPersistingUsers userRepository, EloService eloService) {
        this.userRepository = userRepository;
        this.eloService = eloService;
    }

    @Transactional
    @Override
    public void updateStatsAfterGame(UUID winnerId, UUID loserId) {
        User winner = userRepository.findById(winnerId)
                .orElseThrow(() -> new DataIntegrityException("Winner not found: " + winnerId));
        User loser = userRepository.findById(loserId)
                .orElseThrow(() -> new DataIntegrityException("Loser not found: " + loserId));

        int[] newRatings = eloService.updateRatings(
                winner.getEloRating(), loser.getEloRating(),
                winner.getGamesPlayed(), loser.getGamesPlayed());

        winner.adjustEloRating(newRatings[0]);
        winner.incrementGamesPlayed();
        winner.incrementGamesWon();

        loser.adjustEloRating(newRatings[1]);
        loser.incrementGamesPlayed();

        userRepository.save(winner);
        userRepository.save(loser);
    }
}
