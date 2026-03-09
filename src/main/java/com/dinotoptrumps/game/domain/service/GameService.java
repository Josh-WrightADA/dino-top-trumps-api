package com.dinotoptrumps.game.domain.service;

import com.dinotoptrumps.game.domain.model.Game;
import com.dinotoptrumps.game.domain.model.GameStatus;
import com.dinotoptrumps.game.domain.model.Stat;
import com.dinotoptrumps.game.domain.model.Turn;
import com.dinotoptrumps.game.ports.in.ForCreatingGame;
import com.dinotoptrumps.game.ports.in.ForGettingGameState;
import com.dinotoptrumps.game.ports.in.ForGettingMatchHistory;
import com.dinotoptrumps.game.ports.in.ForJoiningGame;
import com.dinotoptrumps.game.ports.in.ForPlayingTurn;
import com.dinotoptrumps.game.ports.out.ForLoadingCards;
import com.dinotoptrumps.game.ports.out.ForPersistingGames;
import com.dinotoptrumps.game.ports.out.ForPersistingTurns;
import com.dinotoptrumps.game.domain.exception.GameNotFoundException;
import com.dinotoptrumps.game.domain.exception.NotYourTurnException;

import java.util.List;
import java.util.UUID;

public class GameService implements ForCreatingGame, ForJoiningGame, ForPlayingTurn,
        ForGettingGameState, ForGettingMatchHistory {

    private final ForPersistingGames gameRepository;
    private final ForPersistingTurns turnRepository;
    private final ForLoadingCards cardLoader;
    private final DeckService deckService;
    private final StatComparisonService statComparisonService;
    private final EloService eloService;

    public GameService(ForPersistingGames gameRepository, ForPersistingTurns turnRepository,
                       ForLoadingCards cardLoader, DeckService deckService,
                       StatComparisonService statComparisonService, EloService eloService) {
        this.gameRepository = gameRepository;
        this.turnRepository = turnRepository;
        this.cardLoader = cardLoader;
        this.deckService = deckService;
        this.statComparisonService = statComparisonService;
        this.eloService = eloService;
    }

    @Override
    public Game createGame(UUID playerId) {
        Game game = Game.create(playerId);
        return gameRepository.save(game);
    }

    @Override
    public Game joinGame(UUID gameId, UUID playerId) {
        // TODO: Find the game, set player2, deal cards, set status to IN_PROGRESS
        Game game = gameRepository.findById(gameId)
                .orElseThrow(() -> new GameNotFoundException("Game not found: " + gameId));

        // TODO: Validate game is in WAITING status and player is not player1
        // TODO: Deal cards and start the game

        return gameRepository.save(game);
    }

    @Override
    public Turn playTurn(UUID gameId, UUID playerId, Stat chosenStat) {
        // TODO: Validate it's the player's turn
        // TODO: Get top cards from both hands
        // TODO: Compare chosen stat
        // TODO: Award cards to winner
        // TODO: Check if game is over
        // TODO: Persist turn and updated game state
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public Game getGameState(UUID gameId) {
        return gameRepository.findById(gameId)
                .orElseThrow(() -> new GameNotFoundException("Game not found: " + gameId));
    }

    @Override
    public List<Game> getMatchHistory(UUID playerId) {
        return gameRepository.findByPlayerIdAndStatus(playerId, GameStatus.FINISHED);
    }
}
