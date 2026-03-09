package com.dinotoptrumps.game.ports.out;

import com.dinotoptrumps.game.domain.model.Card;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ForLoadingCards {
    List<Card> loadAllCards();
    Optional<Card> findById(UUID id);
}
