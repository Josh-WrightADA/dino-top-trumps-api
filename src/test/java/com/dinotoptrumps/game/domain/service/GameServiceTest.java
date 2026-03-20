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
}
