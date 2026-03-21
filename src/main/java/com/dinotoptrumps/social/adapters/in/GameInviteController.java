package com.dinotoptrumps.social.adapters.in;

import com.dinotoptrumps.social.adapters.in.dto.GameInviteResponse;
import com.dinotoptrumps.social.domain.model.GameInvite;
import com.dinotoptrumps.social.ports.in.ForManagingGameInvites;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/games")
public class GameInviteController {

    private final ForManagingGameInvites forManagingGameInvites;

    public GameInviteController(ForManagingGameInvites forManagingGameInvites) {
        this.forManagingGameInvites = forManagingGameInvites;
    }

    @PostMapping("/{gameId}/invite/{userId}")
    public ResponseEntity<GameInviteResponse> sendInvite(
            @PathVariable UUID gameId,
            @PathVariable UUID userId,
            Authentication authentication) {
        UUID inviterId = (UUID) authentication.getPrincipal();
        GameInvite invite = forManagingGameInvites.sendInvite(inviterId, userId, gameId);
        return ResponseEntity.status(HttpStatus.CREATED).body(GameInviteResponse.from(invite));
    }

    @GetMapping("/invites")
    public ResponseEntity<List<GameInviteResponse>> getPendingInvites(Authentication authentication) {
        UUID userId = (UUID) authentication.getPrincipal();
        List<GameInviteResponse> invites = forManagingGameInvites.getPendingInvites(userId).stream()
                .map(GameInviteResponse::from)
                .toList();
        return ResponseEntity.ok(invites);
    }

    @PutMapping("/invites/{inviteId}/accept")
    public ResponseEntity<GameInviteResponse> acceptInvite(
            @PathVariable UUID inviteId,
            Authentication authentication) {
        UUID userId = (UUID) authentication.getPrincipal();
        GameInvite invite = forManagingGameInvites.acceptInvite(inviteId, userId);
        return ResponseEntity.ok(GameInviteResponse.from(invite));
    }

    @PutMapping("/invites/{inviteId}/decline")
    public ResponseEntity<GameInviteResponse> declineInvite(
            @PathVariable UUID inviteId,
            Authentication authentication) {
        UUID userId = (UUID) authentication.getPrincipal();
        GameInvite invite = forManagingGameInvites.declineInvite(inviteId, userId);
        return ResponseEntity.ok(GameInviteResponse.from(invite));
    }
}
