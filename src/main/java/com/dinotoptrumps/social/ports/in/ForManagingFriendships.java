package com.dinotoptrumps.social.ports.in;

import com.dinotoptrumps.social.domain.model.Friendship;

import java.util.List;
import java.util.UUID;

public interface ForManagingFriendships {
    Friendship sendFriendRequest(UUID requesterId, UUID addresseeId);
    Friendship acceptFriendRequest(UUID friendshipId, UUID acceptingUserId);
    Friendship declineFriendRequest(UUID friendshipId, UUID decliningUserId);
    void removeFriend(UUID friendshipId, UUID removingUserId);
    List<Friendship> getFriends(UUID userId);
    List<Friendship> getPendingRequests(UUID userId);
}
