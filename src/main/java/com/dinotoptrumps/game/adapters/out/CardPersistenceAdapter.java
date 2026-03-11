package com.dinotoptrumps.game.adapters.out;

import com.dinotoptrumps.game.domain.model.Card;
import com.dinotoptrumps.game.ports.out.ForLoadingCards;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Component
public class CardPersistenceAdapter implements ForLoadingCards {

    private final CardJpaRepository cardJpaRepository;

    public CardPersistenceAdapter(CardJpaRepository cardJpaRepository) {
        this.cardJpaRepository = cardJpaRepository;
    }

    @Override
    public List<Card> loadAllCards() {
        return cardJpaRepository.findAll()
                .stream()
                .map(CardMapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Card> findById(UUID id) {
        return cardJpaRepository.findById(id)
                .map(CardMapper::toDomain);
    }
}
