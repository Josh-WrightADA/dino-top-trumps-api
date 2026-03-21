package com.dinotoptrumps.social.domain.service;

import com.dinotoptrumps.auth.domain.model.AccountStatus;
import com.dinotoptrumps.auth.domain.model.Role;
import com.dinotoptrumps.auth.domain.model.User;
import com.dinotoptrumps.auth.ports.out.ForPersistingUsers;
import com.dinotoptrumps.social.domain.exception.CannotFriendYourselfException;
import com.dinotoptrumps.social.domain.exception.FriendRequestAlreadyExistsException;
import com.dinotoptrumps.social.domain.exception.FriendshipNotFoundException;
import com.dinotoptrumps.social.domain.model.Friendship;
import com.dinotoptrumps.social.domain.model.FriendshipStatus;
import com.dinotoptrumps.social.ports.out.ForPersistingFriendships;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

class FriendshipServiceTest {

    private ForPersistingFriendships friendshipRepo;
    private ForPersistingUsers userRepo;
    private FriendshipService service;

    private final UUID requesterId = UUID.randomUUID();
    private final UUID addresseeId = UUID.randomUUID();

    @BeforeEach
    void setUp() {
        friendshipRepo = mock(ForPersistingFriendships.class);
        userRepo = mock(ForPersistingUsers.class);
        service = new FriendshipService(friendshipRepo, userRepo);

        when(friendshipRepo.save(any(Friendship.class))).thenAnswer(inv -> inv.getArgument(0));
    }

    private User activeUser(UUID id) {
        return new User(id, "user_" + id, id + "@test.com", "hash", "Display", "", null, null,
                Role.PLAYER, AccountStatus.ACTIVE, 1000, 0, 0, Instant.now(), Instant.now());
    }

    private User bannedUser(UUID id) {
        return new User(id, "banned_" + id, id + "@test.com", "hash", "Banned", "", null, null,
                Role.PLAYER, AccountStatus.BANNED, 1000, 0, 0, Instant.now(), Instant.now());
    }

    @Test
    void sendFriendRequest_createsRequest() {
        when(userRepo.findById(addresseeId)).thenReturn(Optional.of(activeUser(addresseeId)));
        when(friendshipRepo.findByRequesterAndAddressee(requesterId, addresseeId)).thenReturn(Optional.empty());
        when(friendshipRepo.findByRequesterAndAddressee(addresseeId, requesterId)).thenReturn(Optional.empty());

        Friendship result = service.sendFriendRequest(requesterId, addresseeId);

        assertEquals(FriendshipStatus.PENDING, result.getStatus());
        assertEquals(requesterId, result.getRequesterId());
        assertEquals(addresseeId, result.getAddresseeId());
        verify(friendshipRepo).save(any(Friendship.class));
    }

    @Test
    void sendFriendRequest_cannotFriendSelf() {
        assertThrows(CannotFriendYourselfException.class,
                () -> service.sendFriendRequest(requesterId, requesterId));
    }

    @Test
    void sendFriendRequest_duplicateBlocked() {
        when(userRepo.findById(addresseeId)).thenReturn(Optional.of(activeUser(addresseeId)));

        Friendship existing = Friendship.create(requesterId, addresseeId);
        when(friendshipRepo.findByRequesterAndAddressee(requesterId, addresseeId))
                .thenReturn(Optional.of(existing));
        when(friendshipRepo.findByRequesterAndAddressee(addresseeId, requesterId))
                .thenReturn(Optional.empty());

        assertThrows(FriendRequestAlreadyExistsException.class,
                () -> service.sendFriendRequest(requesterId, addresseeId));
    }

    @Test
    void sendFriendRequest_bannedUserBlocked() {
        when(userRepo.findById(addresseeId)).thenReturn(Optional.of(bannedUser(addresseeId)));
        when(friendshipRepo.findByRequesterAndAddressee(any(), any())).thenReturn(Optional.empty());

        assertThrows(IllegalStateException.class,
                () -> service.sendFriendRequest(requesterId, addresseeId));
    }

    @Test
    void acceptFriendRequest_acceptsRequest() {
        Friendship pending = Friendship.create(requesterId, addresseeId);
        when(friendshipRepo.findById(pending.getId())).thenReturn(Optional.of(pending));

        Friendship result = service.acceptFriendRequest(pending.getId(), addresseeId);

        assertEquals(FriendshipStatus.ACCEPTED, result.getStatus());
        verify(friendshipRepo).save(any(Friendship.class));
    }

    @Test
    void acceptFriendRequest_nonAddresseeRejected() {
        UUID outsider = UUID.randomUUID();
        Friendship pending = Friendship.create(requesterId, addresseeId);
        when(friendshipRepo.findById(pending.getId())).thenReturn(Optional.of(pending));

        assertThrows(IllegalArgumentException.class,
                () -> service.acceptFriendRequest(pending.getId(), outsider));
    }

    @Test
    void declineFriendRequest_declinesRequest() {
        Friendship pending = Friendship.create(requesterId, addresseeId);
        when(friendshipRepo.findById(pending.getId())).thenReturn(Optional.of(pending));

        Friendship result = service.declineFriendRequest(pending.getId(), addresseeId);

        assertEquals(FriendshipStatus.DECLINED, result.getStatus());
        verify(friendshipRepo).save(any(Friendship.class));
    }

    @Test
    void removeFriend_removesAcceptedFriendship() {
        Friendship pending = Friendship.create(requesterId, addresseeId);
        pending.accept();
        when(friendshipRepo.findById(pending.getId())).thenReturn(Optional.of(pending));

        service.removeFriend(pending.getId(), requesterId);

        assertEquals(FriendshipStatus.REMOVED, pending.getStatus());
        verify(friendshipRepo).save(any(Friendship.class));
    }
}
