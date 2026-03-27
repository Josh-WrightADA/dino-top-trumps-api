package com.dinotoptrumps.social.adapters.in;

import com.dinotoptrumps.social.adapters.in.dto.FriendshipResponse;
import com.dinotoptrumps.social.domain.model.Friendship;
import com.dinotoptrumps.social.ports.in.ForManagingFriendships;
import com.dinotoptrumps.social.ports.out.ForLookingUpUsers;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/friends")
public class FriendshipController {

    private final ForManagingFriendships forManagingFriendships;
    private final ForLookingUpUsers userLookup;

    public FriendshipController(ForManagingFriendships forManagingFriendships,
                                ForLookingUpUsers userLookup) {
        this.forManagingFriendships = forManagingFriendships;
        this.userLookup = userLookup;
    }

    @PostMapping("/request/{userId}")
    public ResponseEntity<FriendshipResponse> sendFriendRequest(
            @PathVariable UUID userId,
            Authentication authentication) {
        UUID requesterId = (UUID) authentication.getPrincipal();
        Friendship friendship = forManagingFriendships.sendFriendRequest(requesterId, userId);
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(friendship));
    }

    @PutMapping("/{friendshipId}/accept")
    public ResponseEntity<FriendshipResponse> acceptFriendRequest(
            @PathVariable UUID friendshipId,
            Authentication authentication) {
        UUID userId = (UUID) authentication.getPrincipal();
        Friendship friendship = forManagingFriendships.acceptFriendRequest(friendshipId, userId);
        return ResponseEntity.ok(toResponse(friendship));
    }

    @PutMapping("/{friendshipId}/decline")
    public ResponseEntity<FriendshipResponse> declineFriendRequest(
            @PathVariable UUID friendshipId,
            Authentication authentication) {
        UUID userId = (UUID) authentication.getPrincipal();
        Friendship friendship = forManagingFriendships.declineFriendRequest(friendshipId, userId);
        return ResponseEntity.ok(toResponse(friendship));
    }

    @DeleteMapping("/{friendshipId}")
    public ResponseEntity<Void> removeFriend(
            @PathVariable UUID friendshipId,
            Authentication authentication) {
        UUID userId = (UUID) authentication.getPrincipal();
        forManagingFriendships.removeFriend(friendshipId, userId);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/")
    public ResponseEntity<List<FriendshipResponse>> getFriends(Authentication authentication) {
        UUID userId = (UUID) authentication.getPrincipal();
        List<FriendshipResponse> friends = forManagingFriendships.getFriends(userId).stream()
                .map(this::toResponse)
                .toList();
        return ResponseEntity.ok(friends);
    }

    @GetMapping("/requests")
    public ResponseEntity<List<FriendshipResponse>> getPendingRequests(Authentication authentication) {
        UUID userId = (UUID) authentication.getPrincipal();
        List<FriendshipResponse> requests = forManagingFriendships.getPendingRequests(userId).stream()
                .map(this::toResponse)
                .toList();
        return ResponseEntity.ok(requests);
    }

    private FriendshipResponse toResponse(Friendship friendship) {
        String requesterName = userLookup.findDisplayNameById(friendship.getRequesterId())
                .orElse("Unknown");
        String addresseeName = userLookup.findDisplayNameById(friendship.getAddresseeId())
                .orElse("Unknown");
        return FriendshipResponse.from(friendship, requesterName, addresseeName);
    }
}
