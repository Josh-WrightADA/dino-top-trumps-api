package com.dinotoptrumps.social.adapters.out;

import com.dinotoptrumps.game.ports.in.ForJoiningGame;
import com.dinotoptrumps.social.ports.out.ForJoiningGameFromInvite;
import org.springframework.stereotype.Component;

import java.util.UUID;

// This adapter bridges the social→game bounded context boundary.
// The domain service depends on ForJoiningGameFromInvite (social port),
// and this adapter delegates to ForJoiningGame (game port) in the integration layer.
@Component
public class GameJoinAdapter implements ForJoiningGameFromInvite {

    private final ForJoiningGame forJoiningGame;

    public GameJoinAdapter(ForJoiningGame forJoiningGame) {
        this.forJoiningGame = forJoiningGame;
    }

    @Override
    public void joinGame(UUID gameId, UUID playerId) {
        forJoiningGame.joinGame(gameId, playerId);
    }
}
