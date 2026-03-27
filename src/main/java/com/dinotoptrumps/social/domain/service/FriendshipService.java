package com.dinotoptrumps.social.domain.service;

import com.dinotoptrumps.shared.exception.NotAuthorisedException;
import com.dinotoptrumps.social.domain.exception.CannotFriendYourselfException;
import com.dinotoptrumps.social.domain.exception.FriendRequestAlreadyExistsException;
import com.dinotoptrumps.social.domain.exception.FriendshipNotFoundException;
import com.dinotoptrumps.social.domain.model.Friendship;
import com.dinotoptrumps.social.domain.model.FriendshipStatus;
import com.dinotoptrumps.social.ports.in.ForManagingFriendships;
import com.dinotoptrumps.social.ports.out.ForLookingUpUsers;
import com.dinotoptrumps.social.ports.out.ForPersistingFriendships;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class FriendshipService implements ForManagingFriendships {

    private static final Logger log = LoggerFactory.getLogger(FriendshipService.class);

    private final ForPersistingFriendships friendshipRepo;
    private final ForLookingUpUsers userLookup;

    public FriendshipService(ForPersistingFriendships friendshipRepo, ForLookingUpUsers userLookup) {
        this.friendshipRepo = friendshipRepo;
        this.userLookup = userLookup;
    }

    @Override
    public Friendship sendFriendRequest(UUID requesterId, UUID addresseeId) {
        if (requesterId.equals(addresseeId)) {
            throw new CannotFriendYourselfException("You cannot send a friend request to yourself");
        }

        if (!userLookup.userExistsAndIsActive(addresseeId)) {
            throw new IllegalArgumentException("User not found or not available");
        }

        Optional<Friendship> existingAB = friendshipRepo.findByRequesterAndAddressee(requesterId, addresseeId);
        Optional<Friendship> existingBA = friendshipRepo.findByRequesterAndAddressee(addresseeId, requesterId);

        boolean pendingOrAcceptedAB = existingAB
                .map(f -> f.getStatus() == FriendshipStatus.PENDING || f.getStatus() == FriendshipStatus.ACCEPTED)
                .orElse(false);
        boolean pendingOrAcceptedBA = existingBA
                .map(f -> f.getStatus() == FriendshipStatus.PENDING || f.getStatus() == FriendshipStatus.ACCEPTED)
                .orElse(false);

        if (pendingOrAcceptedAB || pendingOrAcceptedBA) {
            throw new FriendRequestAlreadyExistsException(
                    "A friend request or friendship already exists between these users");
        }

        Friendship friendship = Friendship.create(requesterId, addresseeId);
        Friendship saved = friendshipRepo.save(friendship);
        log.info("event_type=FRIEND_REQUEST_SENT requesterId={} addresseeId={}", requesterId, addresseeId);
        return saved;
    }

    @Override
    public Friendship acceptFriendRequest(UUID friendshipId, UUID acceptingUserId) {
        Friendship friendship = friendshipRepo.findById(friendshipId)
                .orElseThrow(() -> new FriendshipNotFoundException("Friendship not found: " + friendshipId));

        if (!friendship.isAddressedTo(acceptingUserId)) {
            throw new NotAuthorisedException("Not authorised to accept this request");
        }

        friendship.accept();
        Friendship saved = friendshipRepo.save(friendship);
        log.info("event_type=FRIEND_REQUEST_ACCEPTED friendshipId={} acceptingUserId={}",
                friendshipId, acceptingUserId);
        return saved;
    }

    @Override
    public Friendship declineFriendRequest(UUID friendshipId, UUID decliningUserId) {
        Friendship friendship = friendshipRepo.findById(friendshipId)
                .orElseThrow(() -> new FriendshipNotFoundException("Friendship not found: " + friendshipId));

        if (!friendship.isAddressedTo(decliningUserId)) {
            throw new NotAuthorisedException("Not authorised to decline this request");
        }

        friendship.decline();
        Friendship saved = friendshipRepo.save(friendship);
        log.info("event_type=FRIEND_REQUEST_DECLINED friendshipId={} decliningUserId={}",
                friendshipId, decliningUserId);
        return saved;
    }

    @Override
    public void removeFriend(UUID friendshipId, UUID removingUserId) {
        Friendship friendship = friendshipRepo.findById(friendshipId)
                .orElseThrow(() -> new FriendshipNotFoundException("Friendship not found: " + friendshipId));

        if (!friendship.isParticipant(removingUserId)) {
            throw new NotAuthorisedException("Not a participant in this friendship");
        }

        friendship.remove();
        friendshipRepo.save(friendship);
        log.info("event_type=FRIEND_REMOVED friendshipId={} removingUserId={}", friendshipId, removingUserId);
    }

    @Override
    public List<Friendship> getFriends(UUID userId) {
        return friendshipRepo.findAcceptedByUserId(userId);
    }

    @Override
    public List<Friendship> getPendingRequests(UUID userId) {
        return friendshipRepo.findPendingForAddressee(userId);
    }
}
