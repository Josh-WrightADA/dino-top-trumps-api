package com.dinotoptrumps.game.adapters.out;

import com.dinotoptrumps.game.domain.model.Turn;
import com.dinotoptrumps.game.ports.out.ForPersistingTurns;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class TurnPersistenceAdapter implements ForPersistingTurns {

    private final TurnJpaRepository turnJpaRepository;

    public TurnPersistenceAdapter(TurnJpaRepository turnJpaRepository) {
        this.turnJpaRepository = turnJpaRepository;
    }

    @Override
    public Turn save(Turn turn) {
        TurnJpaEntity entity = TurnMapper.toEntity(turn);
        TurnJpaEntity saved = turnJpaRepository.save(entity);
        return TurnMapper.toDomain(saved);
    }

    @Override
    public List<Turn> findByGameId(UUID gameId) {
        return turnJpaRepository.findByGameIdOrderByTurnNumberAsc(gameId)
                .stream()
                .map(TurnMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public int countByGameId(UUID gameId) {
        return turnJpaRepository.countByGameId(gameId);
    }
}
