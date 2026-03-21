package com.dinotoptrumps.social.domain.service;

import com.dinotoptrumps.game.domain.exception.InvalidGameStateException;
import com.dinotoptrumps.game.domain.model.Game;
import com.dinotoptrumps.game.domain.model.GameStatus;
import com.dinotoptrumps.game.ports.in.ForJoiningGame;
import com.dinotoptrumps.game.ports.out.ForPersistingGames;
import com.dinotoptrumps.social.domain.exception.GameInviteExpiredException;
import com.dinotoptrumps.social.domain.exception.NotFriendsException;
import com.dinotoptrumps.social.domain.model.Friendship;
import com.dinotoptrumps.social.domain.model.GameInvite;
import com.dinotoptrumps.social.domain.model.GameInviteStatus;
import com.dinotoptrumps.social.ports.out.ForPersistingFriendships;
import com.dinotoptrumps.social.ports.out.ForPersistingGameInvites;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class GameInviteServiceTest {

    private ForPersistingGameInvites inviteRepo;
    private ForPersistingFriendships friendshipRepo;
    private ForPersistingGames gameRepo;
    private ForJoiningGame forJoiningGame;
    private GameInviteService service;

    private final UUID hostId = UUID.randomUUID();
    private final UUID guestId = UUID.randomUUID();
    private final UUID gameId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        inviteRepo = mock(ForPersistingGameInvites.class);
        friendshipRepo = mock(ForPersistingFriendships.class);
        gameRepo = mock(ForPersistingGames.class);
        forJoiningGame = mock(ForJoiningGame.class);
        service = new GameInviteService(inviteRepo, friendshipRepo, gameRepo, forJoiningGame);

        when(inviteRepo.save(any(GameInvite.class))).thenAnswer(inv -> inv.getArgument(0));
    }

    private Game waitingGame() {
        Instant now = Instant.now();
        return new Game(gameId, hostId, null, GameStatus.WAITING, null,
                List.of(), List.of(), List.of(), null, null, now, now);
    }

    private Game inProgressGame() {
        Instant now = Instant.now();
        return new Game(gameId, hostId, guestId, GameStatus.IN_PROGRESS, hostId,
                new ArrayList<>(), new ArrayList<>(), new ArrayList<>(),
                null, Instant.now().plusSeconds(30), now, now);
    }

    private Friendship acceptedFriendship() {
        Friendship f = Friendship.create(hostId, guestId);
        f.accept();
        return f;
    }

    @Test
    void sendInvite_createsInvite() {
        when(gameRepo.findById(gameId)).thenReturn(Optional.of(waitingGame()));
        when(friendshipRepo.findAcceptedBetween(hostId, guestId))
                .thenReturn(Optional.of(acceptedFriendship()));
        when(inviteRepo.findPendingByGameAndInvitee(gameId, guestId)).thenReturn(Optional.empty());

        GameInvite result = service.sendInvite(hostId, guestId, gameId);

        assertEquals(GameInviteStatus.PENDING, result.getStatus());
        assertEquals(hostId, result.getInviterId());
        assertEquals(guestId, result.getInviteeId());
        verify(inviteRepo).save(any(GameInvite.class));
    }

    @Test
    void sendInvite_notFriendsBlocked() {
        when(gameRepo.findById(gameId)).thenReturn(Optional.of(waitingGame()));
        when(friendshipRepo.findAcceptedBetween(hostId, guestId)).thenReturn(Optional.empty());

        assertThrows(NotFriendsException.class,
                () -> service.sendInvite(hostId, guestId, gameId));
    }

    @Test
    void sendInvite_gameNotWaitingBlocked() {
        when(gameRepo.findById(gameId)).thenReturn(Optional.of(inProgressGame()));
        when(friendshipRepo.findAcceptedBetween(hostId, guestId))
                .thenReturn(Optional.of(acceptedFriendship()));

        assertThrows(InvalidGameStateException.class,
                () -> service.sendInvite(hostId, guestId, gameId));
    }

    @Test
    void sendInvite_nonHostBlocked() {
        UUID nonHost = UUID.randomUUID();
        when(gameRepo.findById(gameId)).thenReturn(Optional.of(waitingGame()));
        when(friendshipRepo.findAcceptedBetween(nonHost, guestId))
                .thenReturn(Optional.of(Friendship.create(nonHost, guestId)));

        assertThrows(InvalidGameStateException.class,
                () -> service.sendInvite(nonHost, guestId, gameId));
    }

    @Test
    void acceptInvite_joinsGame() {
        GameInvite invite = GameInvite.create(gameId, hostId, guestId);
        when(inviteRepo.findById(invite.getId())).thenReturn(Optional.of(invite));
        when(forJoiningGame.joinGame(gameId, guestId)).thenReturn(inProgressGame());

        GameInvite result = service.acceptInvite(invite.getId(), guestId);

        assertEquals(GameInviteStatus.ACCEPTED, result.getStatus());
        verify(forJoiningGame).joinGame(gameId, guestId);
    }

    @Test
    void acceptInvite_expiredBlocked() {
        Instant pastExpiry = Instant.now().minus(10, ChronoUnit.MINUTES);
        GameInvite invite = new GameInvite(UUID.randomUUID(), gameId, hostId, guestId,
                GameInviteStatus.PENDING, pastExpiry, Instant.now().minus(15, ChronoUnit.MINUTES));
        when(inviteRepo.findById(invite.getId())).thenReturn(Optional.of(invite));

        assertThrows(GameInviteExpiredException.class,
                () -> service.acceptInvite(invite.getId(), guestId));
    }
}
