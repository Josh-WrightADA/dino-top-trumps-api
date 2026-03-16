package com.dinotoptrumps.game.adapters.in;

import com.dinotoptrumps.game.adapters.in.dto.GameStateResponse;
import com.dinotoptrumps.game.adapters.in.dto.MatchHistoryEntry;
import com.dinotoptrumps.game.adapters.in.dto.PlayTurnRequest;
import com.dinotoptrumps.game.adapters.in.dto.TurnResponse;
import com.dinotoptrumps.game.domain.exception.GameNotFoundException;
import com.dinotoptrumps.game.domain.model.Game;
import com.dinotoptrumps.game.domain.model.Turn;
import com.dinotoptrumps.game.ports.in.ForCreatingGame;
import com.dinotoptrumps.game.ports.in.ForGettingGameState;
import com.dinotoptrumps.game.ports.in.ForGettingMatchHistory;
import com.dinotoptrumps.game.ports.in.ForJoiningGame;
import com.dinotoptrumps.game.ports.in.ForPlayingTurn;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/games")
public class GameController {

    private final ForCreatingGame forCreatingGame;
    private final ForJoiningGame forJoiningGame;
    private final ForPlayingTurn forPlayingTurn;
    private final ForGettingGameState forGettingGameState;
    private final ForGettingMatchHistory forGettingMatchHistory;

    public GameController(ForCreatingGame forCreatingGame,
                          ForJoiningGame forJoiningGame,
                          ForPlayingTurn forPlayingTurn,
                          ForGettingGameState forGettingGameState,
                          ForGettingMatchHistory forGettingMatchHistory) {
        this.forCreatingGame = forCreatingGame;
        this.forJoiningGame = forJoiningGame;
        this.forPlayingTurn = forPlayingTurn;
        this.forGettingGameState = forGettingGameState;
        this.forGettingMatchHistory = forGettingMatchHistory;
    }

    @PostMapping
    public ResponseEntity<GameStateResponse> createGame(Authentication authentication) {
        UUID playerId = (UUID) authentication.getPrincipal();
        Game game = forCreatingGame.createGame(playerId);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(GameStateResponse.forPlayer(game, playerId));
    }

    @PostMapping("/{gameId}/join")
    public ResponseEntity<GameStateResponse> joinGame(@PathVariable UUID gameId,
                                                      Authentication authentication) {
        UUID playerId = (UUID) authentication.getPrincipal();
        Game game = forJoiningGame.joinGame(gameId, playerId);
        return ResponseEntity.ok(GameStateResponse.forPlayer(game, playerId));
    }

    @PostMapping("/{gameId}/turns")
    public ResponseEntity<TurnResponse> playTurn(@PathVariable UUID gameId,
                                                 @Valid @RequestBody PlayTurnRequest request,
                                                 Authentication authentication) {
        UUID playerId = (UUID) authentication.getPrincipal();
        Turn turn = forPlayingTurn.playTurn(gameId, playerId, request.stat());
        return ResponseEntity.ok(TurnResponse.from(turn));
    }

    @GetMapping("/{gameId}")
    public ResponseEntity<GameStateResponse> getGameState(@PathVariable UUID gameId,
                                                          Authentication authentication) {
        UUID playerId = (UUID) authentication.getPrincipal();
        Game game = forGettingGameState.getGameState(gameId);
        validatePlayerInGame(game, playerId);
        return ResponseEntity.ok(GameStateResponse.forPlayer(game, playerId));
    }

    @GetMapping("/history")
    public ResponseEntity<List<MatchHistoryEntry>> getMatchHistory(Authentication authentication) {
        UUID playerId = (UUID) authentication.getPrincipal();
        List<Game> games = forGettingMatchHistory.getMatchHistory(playerId);
        List<MatchHistoryEntry> history = games.stream()
                .map(game -> MatchHistoryEntry.from(game, playerId))
                .toList();
        return ResponseEntity.ok(history);
    }

    private void validatePlayerInGame(Game game, UUID playerId) {
        if (!game.isPlayer1(playerId) && !playerId.equals(game.getPlayer2Id())) {
            throw new GameNotFoundException("Game not found: " + game.getId());
        }
    }
}
