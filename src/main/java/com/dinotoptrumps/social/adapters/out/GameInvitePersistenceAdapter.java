package com.dinotoptrumps.social.adapters.out;

import com.dinotoptrumps.social.domain.model.GameInvite;
import com.dinotoptrumps.social.domain.model.GameInviteStatus;
import com.dinotoptrumps.social.ports.out.ForPersistingGameInvites;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Component
public class GameInvitePersistenceAdapter implements ForPersistingGameInvites {

    private final GameInviteJpaRepository gameInviteJpaRepository;

    public GameInvitePersistenceAdapter(GameInviteJpaRepository gameInviteJpaRepository) {
        this.gameInviteJpaRepository = gameInviteJpaRepository;
    }

    @Override
    public GameInvite save(GameInvite invite) {
        GameInviteJpaEntity entity = gameInviteJpaRepository.findById(invite.getId())
                .map(existing -> GameInviteMapper.updateEntity(existing, invite))
                .orElseGet(() -> GameInviteMapper.toEntity(invite));
        GameInviteJpaEntity saved = gameInviteJpaRepository.save(entity);
        return GameInviteMapper.toDomain(saved);
    }

    @Override
    public Optional<GameInvite> findById(UUID id) {
        return gameInviteJpaRepository.findById(id)
                .map(GameInviteMapper::toDomain);
    }

    @Override
    public Optional<GameInvite> findPendingByGameAndInvitee(UUID gameId, UUID inviteeId) {
        return gameInviteJpaRepository.findByGameIdAndInviteeIdAndStatus(
                gameId, inviteeId, GameInviteStatus.PENDING.name())
                .map(GameInviteMapper::toDomain);
    }

    @Override
    public List<GameInvite> findPendingByInviteeId(UUID inviteeId) {
        return gameInviteJpaRepository.findByInviteeIdAndStatus(
                inviteeId, GameInviteStatus.PENDING.name()).stream()
                .map(GameInviteMapper::toDomain)
                .toList();
    }

    @Override
    public List<GameInvite> findExpiredPending(Instant now) {
        return gameInviteJpaRepository.findExpiredPending(now).stream()
                .map(GameInviteMapper::toDomain)
                .toList();
    }
}
