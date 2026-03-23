package com.dinotoptrumps.social.adapters.out;

import com.dinotoptrumps.game.domain.model.GameStatus;
import com.dinotoptrumps.game.ports.out.ForPersistingGames;
import com.dinotoptrumps.social.ports.out.ForCheckingGameStatus;
import org.springframework.stereotype.Component;

import java.util.UUID;

// This adapter bridges the social→game bounded context boundary.
// It accesses game domain models because it is the integration layer.
// In a microservices architecture, this would be an API call instead.
@Component
public class GameStatusAdapter implements ForCheckingGameStatus {

    private final ForPersistingGames gameRepo;

    public GameStatusAdapter(ForPersistingGames gameRepo) {
        this.gameRepo = gameRepo;
    }

    @Override
    public boolean isGameWaitingAndHostedBy(UUID gameId, UUID hostId) {
        return gameRepo.findById(gameId)
                .map(game -> game.getStatus() == GameStatus.WAITING && game.getPlayer1Id().equals(hostId))
                .orElse(false);
    }
}
