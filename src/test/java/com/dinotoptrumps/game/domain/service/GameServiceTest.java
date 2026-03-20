package com.dinotoptrumps.game.domain.service;

import com.dinotoptrumps.game.domain.exception.GameNotFoundException;
import com.dinotoptrumps.game.domain.exception.InvalidGameStateException;
import com.dinotoptrumps.game.domain.exception.NotYourTurnException;
import com.dinotoptrumps.game.domain.model.*;
import com.dinotoptrumps.game.ports.out.ForLoadingCards;
import com.dinotoptrumps.game.ports.out.ForPersistingGames;
import com.dinotoptrumps.game.ports.out.ForPersistingTurns;
import com.dinotoptrumps.game.ports.out.ForUpdatingPlayerStats;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class GameServiceTest {

    private ForPersistingGames gameRepository;
    private ForPersistingTurns turnRepository;
    private ForLoadingCards cardLoader;
    private ForUpdatingPlayerStats playerStats;

    private GameService gameService;

    private final UUID player1Id = UUID.randomUUID();
    private final UUID player2Id = UUID.randomUUID();

    private Card strongDino;
    private Card fastDino;
    private Card balancedDino;
    private Card agileDino;

    @BeforeEach
    void setUp() {
        gameRepository = mock(ForPersistingGames.class);
        turnRepository = mock(ForPersistingTurns.class);
        cardLoader = mock(ForLoadingCards.class);
        playerStats = mock(ForUpdatingPlayerStats.class);

        gameService = new GameService(gameRepository, turnRepository, cardLoader,
                playerStats, new DeckService(), new StatComparisonService(), new EloService());

        strongDino = new Card(UUID.randomUUID(), "T-Rex", "Tyrant Lizard", "Carnivore",
                "Cretaceous", null, "", 60, 80, 50, 40, 95);
        fastDino = new Card(UUID.randomUUID(), "Velociraptor", "Swift Thief", "Carnivore",
                "Cretaceous", null, "", 30, 20, 70, 90, 40);
        balancedDino = new Card(UUID.randomUUID(), "Stego", "Roof Lizard", "Herbivore",
                "Jurassic", null, "", 50, 60, 40, 30, 70);
        agileDino = new Card(UUID.randomUUID(), "Raptor", "Thief", "Carnivore",
                "Cretaceous", null, "", 35, 25, 65, 85, 45);

        when(gameRepository.save(any(Game.class))).thenAnswer(inv -> inv.getArgument(0));
        when(turnRepository.save(any(Turn.class))).thenAnswer(inv -> inv.getArgument(0));
    }

    @Nested
    class JoinGame {

        @Test
        void validJoin_setsPlayer2AndDealsCards() {
            Game waitingGame = Game.create(player1Id);
            when(gameRepository.findById(waitingGame.getId())).thenReturn(Optional.of(waitingGame));
            when(cardLoader.loadAllCards()).thenReturn(List.of(strongDino, fastDino));

            Game result = gameService.joinGame(waitingGame.getId(), player2Id);

            assertEquals(player2Id, result.getPlayer2Id());
            assertEquals(GameStatus.IN_PROGRESS, result.getStatus());
            assertFalse(result.getPlayer1Hand().isEmpty());
            assertFalse(result.getPlayer2Hand().isEmpty());
            assertNotNull(result.getCurrentTurnPlayerId());
        }

        @Test
        void gameNotFound_throwsException() {
            UUID fakeGameId = UUID.randomUUID();
            when(gameRepository.findById(fakeGameId)).thenReturn(Optional.empty());

            assertThrows(GameNotFoundException.class,
                    () -> gameService.joinGame(fakeGameId, player2Id));
        }

        @Test
        void gameNotWaiting_throwsException() {
            Game inProgressGame = createGameInProgress();
            when(gameRepository.findById(inProgressGame.getId())).thenReturn(Optional.of(inProgressGame));

            assertThrows(InvalidGameStateException.class,
                    () -> gameService.joinGame(inProgressGame.getId(), UUID.randomUUID()));
        }

        @Test
        void playerJoinsOwnGame_throwsException() {
            Game waitingGame = Game.create(player1Id);
            when(gameRepository.findById(waitingGame.getId())).thenReturn(Optional.of(waitingGame));

            assertThrows(InvalidGameStateException.class,
                    () -> gameService.joinGame(waitingGame.getId(), player1Id));
        }

        @Test
        void dealsAllCards_noCardsLost() {
            Game waitingGame = Game.create(player1Id);
            when(gameRepository.findById(waitingGame.getId())).thenReturn(Optional.of(waitingGame));
            when(cardLoader.loadAllCards()).thenReturn(List.of(strongDino, fastDino));

            Game result = gameService.joinGame(waitingGame.getId(), player2Id);

            int totalCards = result.getPlayer1Hand().size() + result.getPlayer2Hand().size();
            assertEquals(2, totalCards, "All cards should be dealt between both players");
        }
    }

    @Nested
    class PlayTurn {

        @Test
        void validTurn_returnsCompletedTurn() {
            Game game = setupGameWithMocks(strongDino, fastDino);

            Turn turn = gameService.playTurn(game.getId(), player1Id, Stat.STRENGTH);

            assertNotNull(turn);
            assertEquals(Stat.STRENGTH, turn.getChosenStat());
            assertEquals(game.getId(), turn.getGameId());
        }

        @Test
        void winnerGetsCards_handSizesUpdate() {
            Game game = setupGameWithMocks(strongDino, fastDino);

            gameService.playTurn(game.getId(), player1Id, Stat.STRENGTH);

            verify(gameRepository).save(argThat(savedGame ->
                    savedGame.getPlayer1Hand().size() == 2 && savedGame.getPlayer2Hand().isEmpty()));
        }

        @Test
        void notYourTurn_throwsException() {
            Game game = createGameWithHands(strongDino, fastDino);
            when(gameRepository.findById(game.getId())).thenReturn(Optional.of(game));

            assertThrows(NotYourTurnException.class,
                    () -> gameService.playTurn(game.getId(), player2Id, Stat.SPEED));
        }

        @Test
        void gameNotInProgress_throwsException() {
            Game finishedGame = createFinishedGame();
            when(gameRepository.findById(finishedGame.getId())).thenReturn(Optional.of(finishedGame));

            assertThrows(InvalidGameStateException.class,
                    () -> gameService.playTurn(finishedGame.getId(), player1Id, Stat.SPEED));
        }

        @Test
        void lastCard_gameFinishes() {
            Game game = setupGameWithMocks(strongDino, fastDino);

            gameService.playTurn(game.getId(), player1Id, Stat.STRENGTH);

            verify(gameRepository).save(argThat(savedGame ->
                    savedGame.getStatus() == GameStatus.FINISHED && savedGame.getWinnerId() != null));
        }

        @Test
        void winnerKeepsTurn() {
            Game game = setupMultiCardGameWithMocks();

            gameService.playTurn(game.getId(), player1Id, Stat.STRENGTH);

            verify(gameRepository).save(argThat(savedGame ->
                    savedGame.getCurrentTurnPlayerId().equals(player1Id)));
        }

        @Test
        void loserGetsTurnWhenOpponentWins() {
            Game game = setupMultiCardGameWithMocks();

            gameService.playTurn(game.getId(), player1Id, Stat.SPEED);

            verify(gameRepository).save(argThat(savedGame ->
                    savedGame.getCurrentTurnPlayerId().equals(player2Id)));
        }

        @Test
        void persistsTurnRecord() {
            Game game = setupGameWithMocks(strongDino, fastDino);

            gameService.playTurn(game.getId(), player1Id, Stat.STRENGTH);

            verify(turnRepository).save(any(Turn.class));
        }

        @Test
        void gameFinished_updatesPlayerStats() {
            Game game = setupGameWithMocks(strongDino, fastDino);

            gameService.playTurn(game.getId(), player1Id, Stat.STRENGTH);

            verify(playerStats).updateStatsAfterGame(player1Id, player2Id);
        }

        @Test
        void gameNotFinished_doesNotUpdatePlayerStats() {
            Game game = setupMultiCardGameWithMocks();

            gameService.playTurn(game.getId(), player1Id, Stat.STRENGTH);

            verify(playerStats, never()).updateStatsAfterGame(any(), any());
        }

        private Game setupGameWithMocks(Card p1Card, Card p2Card) {
            Game game = createGameWithHands(p1Card, p2Card);
            when(gameRepository.findById(game.getId())).thenReturn(Optional.of(game));
            when(cardLoader.findById(p1Card.getId())).thenReturn(Optional.of(p1Card));
            when(cardLoader.findById(p2Card.getId())).thenReturn(Optional.of(p2Card));
            when(turnRepository.countByGameId(game.getId())).thenReturn(0);
            return game;
        }

        private Game setupMultiCardGameWithMocks() {
            Game game = createGameWithMultipleCards(
                    List.of(strongDino.getId(), balancedDino.getId()),
                    List.of(fastDino.getId(), agileDino.getId()));
            when(gameRepository.findById(game.getId())).thenReturn(Optional.of(game));
            when(cardLoader.findById(strongDino.getId())).thenReturn(Optional.of(strongDino));
            when(cardLoader.findById(fastDino.getId())).thenReturn(Optional.of(fastDino));
            when(turnRepository.countByGameId(game.getId())).thenReturn(0);
            return game;
        }
    }

    // --- Test fixtures ---

    private Game createGameInProgress() {
        Instant now = Instant.now();
        return new Game(UUID.randomUUID(), player1Id, player2Id, GameStatus.IN_PROGRESS,
                player1Id, List.of(strongDino.getId()), List.of(fastDino.getId()),
                new ArrayList<>(), null, Instant.now().plusSeconds(30), now, now);
    }

    private Game createGameWithHands(Card p1Card, Card p2Card) {
        Instant now = Instant.now();
        return new Game(UUID.randomUUID(), player1Id, player2Id, GameStatus.IN_PROGRESS,
                player1Id,
                new ArrayList<>(List.of(p1Card.getId())),
                new ArrayList<>(List.of(p2Card.getId())),
                new ArrayList<>(), null, Instant.now().plusSeconds(30), now, now);
    }

    private Game createGameWithMultipleCards(List<UUID> p1Cards, List<UUID> p2Cards) {
        Instant now = Instant.now();
        return new Game(UUID.randomUUID(), player1Id, player2Id, GameStatus.IN_PROGRESS,
                player1Id,
                new ArrayList<>(p1Cards),
                new ArrayList<>(p2Cards),
                new ArrayList<>(), null, Instant.now().plusSeconds(30), now, now);
    }

    private Game createFinishedGame() {
        Instant now = Instant.now();
        return new Game(UUID.randomUUID(), player1Id, player2Id, GameStatus.FINISHED,
                null, List.of(), List.of(),
                List.of(), player1Id, null, now, now);
    }

    @Nested
    class CreateGame {

        @Test
        void createsWaitingGameForPlayer() {
            Game result = gameService.createGame(player1Id);

            verify(gameRepository).save(argThat(game ->
                    game.getPlayer1Id().equals(player1Id)
                            && game.getStatus() == GameStatus.WAITING));
        }
    }

    @Nested
    class ForfeitGame {

        @Test
        void validForfeit_opponentWins() {
            Game game = createGameInProgress();
            when(gameRepository.findById(game.getId())).thenReturn(Optional.of(game));

            Game result = gameService.forfeitGame(game.getId(), player1Id);

            assertEquals(GameStatus.FINISHED, result.getStatus());
            assertEquals(player2Id, result.getWinnerId());
        }

        @Test
        void forfeit_updatesPlayerStats() {
            Game game = createGameInProgress();
            when(gameRepository.findById(game.getId())).thenReturn(Optional.of(game));

            gameService.forfeitGame(game.getId(), player1Id);

            verify(playerStats).updateStatsAfterGame(player2Id, player1Id);
        }

        @Test
        void forfeit_nonParticipant_throwsException() {
            Game game = createGameInProgress();
            when(gameRepository.findById(game.getId())).thenReturn(Optional.of(game));

            UUID outsider = UUID.randomUUID();
            assertThrows(NotYourTurnException.class,
                    () -> gameService.forfeitGame(game.getId(), outsider));
        }

        @Test
        void forfeit_finishedGame_throwsException() {
            Game game = createFinishedGame();
            when(gameRepository.findById(game.getId())).thenReturn(Optional.of(game));

            assertThrows(InvalidGameStateException.class,
                    () -> gameService.forfeitGame(game.getId(), player1Id));
        }
    }

    @Nested
    class TurnTimeout {

        @Test
        void playTurn_whenTimedOut_forfeitsCurrentPlayer() {
            Instant pastDeadline = Instant.now().minusSeconds(60);
            Game game = new Game(UUID.randomUUID(), player1Id, player2Id, GameStatus.IN_PROGRESS,
                    player1Id,
                    new ArrayList<>(List.of(strongDino.getId())),
                    new ArrayList<>(List.of(fastDino.getId())),
                    new ArrayList<>(), null, pastDeadline, Instant.now(), Instant.now());

            when(gameRepository.findById(game.getId())).thenReturn(Optional.of(game));

            assertThrows(InvalidGameStateException.class,
                    () -> gameService.playTurn(game.getId(), player1Id, Stat.STRENGTH));

            verify(gameRepository).save(argThat(saved ->
                    saved.getStatus() == GameStatus.FINISHED
                            && saved.getWinnerId().equals(player2Id)));
            verify(playerStats).updateStatsAfterGame(player2Id, player1Id);
        }
    }

    @Nested
    class CleanupStaleGames {

        @Test
        void cleansUpStaleWaitingGames() {
            Game staleWaiting = Game.create(player1Id);
            when(gameRepository.findStaleGames(any(Instant.class), any(Instant.class)))
                    .thenReturn(List.of(staleWaiting));

            int cleaned = gameService.cleanupStaleGames();

            assertEquals(1, cleaned);
            verify(gameRepository).save(argThat(game ->
                    game.getStatus() == GameStatus.FINISHED));
        }

        @Test
        void cleansUpTimedOutInProgressGames() {
            Instant pastDeadline = Instant.now().minusSeconds(600);
            Game timedOut = new Game(UUID.randomUUID(), player1Id, player2Id,
                    GameStatus.IN_PROGRESS, player1Id,
                    new ArrayList<>(List.of(strongDino.getId())),
                    new ArrayList<>(List.of(fastDino.getId())),
                    new ArrayList<>(), null, pastDeadline, Instant.now(), Instant.now());

            when(gameRepository.findStaleGames(any(Instant.class), any(Instant.class)))
                    .thenReturn(List.of(timedOut));

            int cleaned = gameService.cleanupStaleGames();

            assertEquals(1, cleaned);
            verify(gameRepository).save(argThat(game ->
                    game.getStatus() == GameStatus.FINISHED
                            && game.getWinnerId().equals(player2Id)));
            verify(playerStats).updateStatsAfterGame(player2Id, player1Id);
        }

        @Test
        void noStaleGames_returnsZero() {
            when(gameRepository.findStaleGames(any(Instant.class), any(Instant.class)))
                    .thenReturn(List.of());

            int cleaned = gameService.cleanupStaleGames();

            assertEquals(0, cleaned);
            verify(gameRepository, never()).save(any());
        }
    }

    @Nested
    class GetLastTurn {

        @Test
        void returnsLastTurnWhenTurnsExist() {
            UUID gameId = UUID.randomUUID();
            Turn turn1 = new Turn(UUID.randomUUID(), gameId, 1, player1Id,
                    strongDino.getId(), fastDino.getId(), Stat.STRENGTH, 95, 40,
                    player1Id, Instant.now());
            Turn turn2 = new Turn(UUID.randomUUID(), gameId, 2, player1Id,
                    balancedDino.getId(), agileDino.getId(), Stat.SPEED, 30, 85,
                    player2Id, Instant.now());

            when(turnRepository.findByGameId(gameId)).thenReturn(List.of(turn1, turn2));

            Optional<Turn> result = gameService.getLastTurn(gameId);

            assertTrue(result.isPresent());
            assertEquals(2, result.get().getTurnNumber());
        }

        @Test
        void returnsEmptyWhenNoTurns() {
            UUID gameId = UUID.randomUUID();
            when(turnRepository.findByGameId(gameId)).thenReturn(List.of());

            Optional<Turn> result = gameService.getLastTurn(gameId);

            assertTrue(result.isEmpty());
        }
    }
}
