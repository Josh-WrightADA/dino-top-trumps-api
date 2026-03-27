package com.dinotoptrumps.social.adapters.out;

import com.dinotoptrumps.social.domain.model.GameInvite;
import com.dinotoptrumps.social.domain.model.GameInviteStatus;

public class GameInviteMapper {

    public static GameInviteJpaEntity toEntity(GameInvite invite) {
        GameInviteJpaEntity entity = new GameInviteJpaEntity();
        entity.setId(invite.getId());
        return updateEntity(entity, invite);
    }

    public static GameInviteJpaEntity updateEntity(GameInviteJpaEntity entity, GameInvite invite) {
        entity.setGameId(invite.getGameId());
        entity.setInviterId(invite.getInviterId());
        entity.setInviteeId(invite.getInviteeId());
        entity.setStatus(invite.getStatus().name());
        entity.setExpiresAt(invite.getExpiresAt());
        entity.setCreatedAt(invite.getCreatedAt());
        return entity;
    }

    public static GameInvite toDomain(GameInviteJpaEntity entity) {
        return new GameInvite(
                entity.getId(),
                entity.getGameId(),
                entity.getInviterId(),
                entity.getInviteeId(),
                GameInviteStatus.valueOf(entity.getStatus()),
                entity.getExpiresAt(),
                entity.getCreatedAt()
        );
    }
}
