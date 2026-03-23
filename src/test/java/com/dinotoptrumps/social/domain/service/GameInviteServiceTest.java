package com.dinotoptrumps.social.domain.service;

import com.dinotoptrumps.game.ports.in.ForJoiningGame;
import com.dinotoptrumps.social.domain.exception.GameInviteExpiredException;
import com.dinotoptrumps.social.domain.exception.NotFriendsException;
import com.dinotoptrumps.social.domain.model.Friendship;
import com.dinotoptrumps.social.domain.model.GameInvite;
import com.dinotoptrumps.social.domain.model.GameInviteStatus;
import com.dinotoptrumps.social.ports.out.ForCheckingGameStatus;
import com.dinotoptrumps.social.ports.out.ForPersistingFriendships;
import com.dinotoptrumps.social.ports.out.ForPersistingGameInvites;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
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
    private ForCheckingGameStatus gameStatusChecker;
    private ForJoiningGame forJoiningGame;
    private GameInviteService service;

    private final UUID hostId = UUID.randomUUID();
    private final UUID guestId = UUID.randomUUID();
    private final UUID gameId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        inviteRepo = mock(ForPersistingGameInvites.class);
        friendshipRepo = mock(ForPersistingFriendships.class);
        gameStatusChecker = mock(ForCheckingGameStatus.class);
        forJoiningGame = mock(ForJoiningGame.class);
        service = new GameInviteService(inviteRepo, friendshipRepo, gameStatusChecker, forJoiningGame);

        when(inviteRepo.save(any(GameInvite.class))).thenAnswer(inv -> inv.getArgument(0));
    }

    private Friendship acceptedFriendship() {
        Friendship f = Friendship.create(hostId, guestId);
        f.accept();
        return f;
    }

    private GameInvite pendingInvite() {
        return GameInvite.create(gameId, hostId, guestId);
    }

    private GameInvite expiredInvite() {
        Instant pastExpiry = Instant.now().minus(10, ChronoUnit.MINUTES);
        return new GameInvite(UUID.randomUUID(), gameId, hostId, guestId,
                GameInviteStatus.PENDING, pastExpiry, Instant.now().minus(15, ChronoUnit.MINUTES));
    }

    @Test
    void sendInvite_createsInvite() {
        when(gameStatusChecker.isGameWaitingAndHostedBy(gameId, hostId)).thenReturn(true);
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
        when(gameStatusChecker.isGameWaitingAndHostedBy(gameId, hostId)).thenReturn(true);
        when(friendshipRepo.findAcceptedBetween(hostId, guestId)).thenReturn(Optional.empty());

        assertThrows(NotFriendsException.class,
                () -> service.sendInvite(hostId, guestId, gameId));
    }

    @Test
    void sendInvite_gameNotAvailableBlocked() {
        when(gameStatusChecker.isGameWaitingAndHostedBy(gameId, hostId)).thenReturn(false);

        assertThrows(IllegalStateException.class,
                () -> service.sendInvite(hostId, guestId, gameId));
    }

    @Test
    void sendInvite_nonHostBlocked() {
        UUID nonHost = UUID.randomUUID();
        when(gameStatusChecker.isGameWaitingAndHostedBy(gameId, nonHost)).thenReturn(false);

        assertThrows(IllegalStateException.class,
                () -> service.sendInvite(nonHost, guestId, gameId));
    }

    @Test
    void acceptInvite_joinsGame() {
        GameInvite invite = pendingInvite();
        when(inviteRepo.findById(invite.getId())).thenReturn(Optional.of(invite));
        when(forJoiningGame.joinGame(gameId, guestId)).thenReturn(null);

        GameInvite result = service.acceptInvite(invite.getId(), guestId);

        assertEquals(GameInviteStatus.ACCEPTED, result.getStatus());
        verify(forJoiningGame).joinGame(gameId, guestId);
    }

    @Test
    void acceptInvite_expiredBlocked() {
        GameInvite invite = expiredInvite();
        when(inviteRepo.findById(invite.getId())).thenReturn(Optional.of(invite));

        assertThrows(GameInviteExpiredException.class,
                () -> service.acceptInvite(invite.getId(), guestId));
    }
}
