package com.dinotoptrumps.social.adapters.out;

import com.dinotoptrumps.social.domain.model.Friendship;
import com.dinotoptrumps.social.domain.model.FriendshipStatus;

public class FriendshipMapper {

    public static FriendshipJpaEntity toEntity(Friendship friendship) {
        FriendshipJpaEntity entity = new FriendshipJpaEntity();
        entity.setId(friendship.getId());
        entity.setRequesterId(friendship.getRequesterId());
        entity.setAddresseeId(friendship.getAddresseeId());
        entity.setStatus(friendship.getStatus().name());
        entity.setCreatedAt(friendship.getCreatedAt());
        entity.setUpdatedAt(friendship.getUpdatedAt());
        return entity;
    }

    public static Friendship toDomain(FriendshipJpaEntity entity) {
        return new Friendship(
                entity.getId(),
                entity.getRequesterId(),
                entity.getAddresseeId(),
                FriendshipStatus.valueOf(entity.getStatus()),
                entity.getCreatedAt(),
                entity.getUpdatedAt()
        );
    }
}
