package com.dinotoptrumps.game.adapters.out;

import com.dinotoptrumps.game.domain.model.Game;
import com.dinotoptrumps.game.domain.model.GameStatus;
import com.dinotoptrumps.game.ports.out.ForPersistingGames;
import org.springframework.stereotype.Component;

import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class GamePersistenceAdapter implements ForPersistingGames {

    private final GameJpaRepository gameJpaRepository;

    public GamePersistenceAdapter(GameJpaRepository gameJpaRepository) {
        this.gameJpaRepository = gameJpaRepository;
    }

    @Override
    public Game save(Game game) {
        GameJpaEntity entity = GameMapper.toEntity(game);
        GameJpaEntity saved = gameJpaRepository.save(entity);
        return GameMapper.toDomain(saved);
    }

    @Override
    public Optional<Game> findById(UUID id) {
        return gameJpaRepository.findById(id)
                .map(GameMapper::toDomain);
    }

    @Override
    public List<Game> findByPlayerIdAndStatus(UUID playerId, GameStatus status) {
        return gameJpaRepository.findByPlayerIdAndStatus(playerId, status.name())
                .stream()
                .map(GameMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Game> findByStatus(GameStatus status) {
        return gameJpaRepository.findByStatus(status.name())
                .stream()
                .map(GameMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Game> findActiveByPlayerId(UUID playerId) {
        List<String> activeStatuses = List.of(
                GameStatus.WAITING.name(),
                GameStatus.IN_PROGRESS.name());
        return gameJpaRepository.findByPlayerIdAndStatusIn(playerId, activeStatuses)
                .stream()
                .map(GameMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public List<Game> findStaleGames(Instant waitingBefore, Instant timedOutBefore) {
        return gameJpaRepository.findStaleGames(waitingBefore, timedOutBefore)
                .stream()
                .map(GameMapper::toDomain)
                .collect(Collectors.toList());
    }
}
