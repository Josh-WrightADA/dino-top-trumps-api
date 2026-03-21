package com.dinotoptrumps.social.adapters.out;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "game_invites")
@Getter
@Setter
@NoArgsConstructor
public class GameInviteJpaEntity {

    @Id
    private UUID id;

    @Column(name = "game_id", nullable = false)
    private UUID gameId;

    @Column(name = "inviter_id", nullable = false)
    private UUID inviterId;

    @Column(name = "invitee_id", nullable = false)
    private UUID inviteeId;

    @Column(nullable = false, length = 20)
    private String status;

    @Column(name = "expires_at", nullable = false)
    private Instant expiresAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    private Instant createdAt;
}
