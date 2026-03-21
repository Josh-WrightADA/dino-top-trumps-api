package com.dinotoptrumps.auth.adapters.out;

import com.dinotoptrumps.auth.domain.model.User;

public class UserMapper {

    public static UserJpaEntity toEntity(User user) {
        // TODO: Map domain User to JPA entity
        UserJpaEntity entity = new UserJpaEntity();
        entity.setId(user.getId());
        entity.setUsername(user.getUsername());
        entity.setEmail(user.getEmail());
        entity.setPasswordHash(user.getPasswordHash());
        entity.setDisplayName(user.getDisplayName());
        entity.setAvatarUrl(user.getAvatarUrl());
        entity.setBio(user.getBio());
        entity.setFavouriteCardId(user.getFavouriteCardId());
        entity.setEloRating(user.getEloRating());
        entity.setGamesPlayed(user.getGamesPlayed());
        entity.setGamesWon(user.getGamesWon());
        entity.setCreatedAt(user.getCreatedAt());
        entity.setUpdatedAt(user.getUpdatedAt());
        return entity;
    }

    public static User toDomain(UserJpaEntity entity) {
        return new User(
                entity.getId(),
                entity.getUsername(),
                entity.getEmail(),
                entity.getPasswordHash(),
                entity.getDisplayName(),
                entity.getAvatarUrl() != null ? entity.getAvatarUrl() : "",
                entity.getBio(),
                entity.getFavouriteCardId(),
                entity.getEloRating(),
                entity.getGamesPlayed(),
                entity.getGamesWon(),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
