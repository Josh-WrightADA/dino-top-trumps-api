package com.dinotoptrumps.game.adapters.in;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/games")
public class GameController {

    // TODO: Inject ForCreatingGame, ForJoiningGame, ForPlayingTurn, ForGettingGameState, ForGettingMatchHistory

    @PostMapping
    public void createGame() {
        // TODO: Create a new game for authenticated player
    }

    @PostMapping("/{gameId}/join")
    public void joinGame(@PathVariable UUID gameId) {
        // TODO: Join an existing game
    }

    @PostMapping("/{gameId}/turns")
    public void playTurn(@PathVariable UUID gameId) {
        // TODO: Accept chosen stat, play a turn
    }

    @GetMapping("/{gameId}")
    public void getGameState(@PathVariable UUID gameId) {
        // TODO: Return current game state
    }

    @GetMapping("/history")
    public void getMatchHistory() {
        // TODO: Return match history for authenticated player
    }
}
