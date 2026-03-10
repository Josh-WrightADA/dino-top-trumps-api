package com.dinotoptrumps.auth.adapters.out;

import com.dinotoptrumps.auth.domain.model.PasswordResetToken;

public class PasswordResetTokenMapper {

    public static PasswordResetTokenJpaEntity toEntity(PasswordResetToken token) {
        PasswordResetTokenJpaEntity entity = new PasswordResetTokenJpaEntity();
        entity.setId(token.getId());
        entity.setUserId(token.getUserId());
        entity.setToken(token.getToken());
        entity.setExpiresAt(token.getExpiresAt());
        entity.setUsed(token.isUsed());
        entity.setCreatedAt(token.getCreatedAt());
        return entity;
    }

    public static PasswordResetToken toDomain(PasswordResetTokenJpaEntity entity) {
        return new PasswordResetToken(
                entity.getId(),
                entity.getUserId(),
                entity.getToken(),
                entity.getExpiresAt(),
                entity.isUsed(),
                entity.getCreatedAt()
        );
    }
}
