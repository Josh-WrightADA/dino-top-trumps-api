package com.dinotoptrumps.game.domain.service;

import com.dinotoptrumps.game.domain.exception.InvalidGameStateException;
import com.dinotoptrumps.game.domain.exception.GameNotFoundException;
import com.dinotoptrumps.game.domain.exception.NotYourTurnException;
import com.dinotoptrumps.game.domain.model.Card;
import com.dinotoptrumps.game.domain.model.Game;
import com.dinotoptrumps.game.domain.model.GameStatus;
import com.dinotoptrumps.game.domain.model.Hand;
import com.dinotoptrumps.game.domain.model.Stat;
import com.dinotoptrumps.game.domain.model.Turn;
import com.dinotoptrumps.game.ports.in.ForCreatingGame;
import com.dinotoptrumps.game.ports.in.ForForfeitingGame;
import com.dinotoptrumps.game.ports.in.ForGettingGameState;
import com.dinotoptrumps.game.ports.in.ForGettingMatchHistory;
import com.dinotoptrumps.game.ports.in.ForJoiningGame;
import com.dinotoptrumps.game.ports.in.ForListingGames;
import com.dinotoptrumps.game.ports.in.ForPlayingTurn;
import com.dinotoptrumps.game.ports.out.ForLoadingCards;
import com.dinotoptrumps.game.ports.out.ForPersistingGames;
import com.dinotoptrumps.game.ports.out.ForPersistingTurns;
import com.dinotoptrumps.game.ports.out.ForUpdatingPlayerStats;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class GameService implements ForCreatingGame, ForJoiningGame, ForPlayingTurn,
        ForGettingGameState, ForGettingMatchHistory, ForForfeitingGame, ForListingGames {

    private static final Logger log = LoggerFactory.getLogger(GameService.class);

    private final ForPersistingGames gameRepository;
    private final ForPersistingTurns turnRepository;
    private final ForLoadingCards cardLoader;
    private final ForUpdatingPlayerStats playerStats;
    private final DeckService deckService;
    private final StatComparisonService statComparisonService;
    private final EloService eloService;

    public GameService(ForPersistingGames gameRepository, ForPersistingTurns turnRepository,
                       ForLoadingCards cardLoader, ForUpdatingPlayerStats playerStats,
                       DeckService deckService, StatComparisonService statComparisonService,
                       EloService eloService) {
        this.gameRepository = gameRepository;
        this.turnRepository = turnRepository;
        this.cardLoader = cardLoader;
        this.playerStats = playerStats;
        this.deckService = deckService;
        this.statComparisonService = statComparisonService;
        this.eloService = eloService;
    }

    @Override
    public Game createGame(UUID playerId) {
        Game game = Game.create(playerId);
        Game saved = gameRepository.save(game);
        log.info("Game created: {}", saved.getId());
        return saved;
    }

    @Override
    public Game joinGame(UUID gameId, UUID playerId) {
        Game game = findGameOrThrow(gameId);

        if (game.getStatus() != GameStatus.WAITING) {
            throw new InvalidGameStateException("Game is not in WAITING status");
        }
        if (game.getPlayer1Id().equals(playerId)) {
            throw new InvalidGameStateException("Cannot join own game");
        }

        List<UUID> cardIds = cardLoader.loadAllCards().stream()
                .map(Card::getId)
                .toList();
        Hand[] hands = deckService.deal(cardIds);

        game.start(playerId, hands);

        Game saved = gameRepository.save(game);
        log.info("Game {} started — cards dealt", gameId);
        return saved;
    }

    @Override
    public Turn playTurn(UUID gameId, UUID playerId, Stat chosenStat) {
        Game game = findGameOrThrow(gameId);
        validateGameInProgress(game);
        validatePlayerIsParticipant(game, playerId);
        handleTimeoutIfExpired(game, gameId);
        validatePlayerTurn(game, playerId);

        UUID p1CardId = game.getPlayer1Hand().getFirst();
        UUID p2CardId = game.getPlayer2Hand().getFirst();

        Card p1Card = loadCardOrThrow(p1CardId);
        Card p2Card = loadCardOrThrow(p2CardId);

        UUID winningCardId = statComparisonService.compare(p1Card, p2Card, chosenStat);
        UUID turnWinnerPlayerId = game.resolveRound(winningCardId, p1CardId, p2CardId);
        game.checkGameOver();

        gameRepository.save(game);

        if (game.getStatus() == GameStatus.FINISHED) {
            UUID loserId = game.getWinnerId().equals(game.getPlayer1Id())
                    ? game.getPlayer2Id()
                    : game.getPlayer1Id();
            playerStats.updateStatsAfterGame(game.getWinnerId(), loserId);
            log.info("Game {} finished — winner determined", gameId);
        }

        int turnNumber = turnRepository.countByGameId(gameId) + 1;
        Turn turn = new Turn(
                UUID.randomUUID(),
                gameId,
                turnNumber,
                playerId,
                p1CardId,
                p2CardId,
                chosenStat,
                chosenStat.getValueFromCard(p1Card),
                chosenStat.getValueFromCard(p2Card),
                turnWinnerPlayerId,
                Instant.now()
        );

        Turn saved = turnRepository.save(turn);
        log.info("Turn {} played in game {}", saved.getTurnNumber(), gameId);
        return saved;
    }

    @Override
    public Game forfeitGame(UUID gameId, UUID playerId) {
        Game game = findGameOrThrow(gameId);
        validateGameInProgress(game);
        validatePlayerIsParticipant(game, playerId);

        UUID opponentId = game.isPlayer1(playerId)
                ? game.getPlayer2Id()
                : game.getPlayer1Id();

        game.forfeit(opponentId);
        gameRepository.save(game);
        playerStats.updateStatsAfterGame(opponentId, playerId);
        log.info("Game {} forfeited by player", gameId);
        return game;
    }

    @Override
    public Game getGameState(UUID gameId) {
        return findGameOrThrow(gameId);
    }

    @Override
    public List<Game> getMatchHistory(UUID playerId) {
        return gameRepository.findByPlayerIdAndStatus(playerId, GameStatus.FINISHED);
    }

    @Override
    public List<Game> getAvailableGames() {
        return gameRepository.findByStatus(GameStatus.WAITING);
    }

    @Override
    public List<Game> getActiveGames(UUID playerId) {
        return gameRepository.findActiveByPlayerId(playerId);
    }

    private Game findGameOrThrow(UUID gameId) {
        return gameRepository.findById(gameId)
                .orElseThrow(() -> new GameNotFoundException("Game not found: " + gameId));
    }

    private Card loadCardOrThrow(UUID cardId) {
        return cardLoader.findById(cardId)
                .orElseThrow(() -> new InvalidGameStateException("Card not found: " + cardId));
    }

    private void validateGameInProgress(Game game) {
        if (game.getStatus() != GameStatus.IN_PROGRESS) {
            throw new InvalidGameStateException("Game is not in IN_PROGRESS status");
        }
    }

    private void validatePlayerIsParticipant(Game game, UUID playerId) {
        if (!game.isPlayer1(playerId) && !playerId.equals(game.getPlayer2Id())) {
            throw new NotYourTurnException("Player is not a participant in this game");
        }
    }

    private void validatePlayerTurn(Game game, UUID playerId) {
        if (!game.getCurrentTurnPlayerId().equals(playerId)) {
            throw new NotYourTurnException("Not this player's turn");
        }
    }

    private void handleTimeoutIfExpired(Game game, UUID gameId) {
        if (!game.isTimedOut()) {
            return;
        }
        UUID currentPlayer = game.getCurrentTurnPlayerId();
        UUID opponentId = game.isPlayer1(currentPlayer)
                ? game.getPlayer2Id()
                : game.getPlayer1Id();
        game.forfeit(opponentId);
        gameRepository.save(game);
        playerStats.updateStatsAfterGame(opponentId, currentPlayer);
        log.info("Game {} forfeited due to timeout", gameId);
        throw new InvalidGameStateException("Turn timed out — game forfeited");
    }
}
