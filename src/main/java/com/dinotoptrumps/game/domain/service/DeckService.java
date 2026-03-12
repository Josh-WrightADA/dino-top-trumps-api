package com.dinotoptrumps.game.domain.service;

import com.dinotoptrumps.game.domain.model.Hand;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class DeckService {

    /**
     * Shuffles the given card IDs and deals them evenly into two hands.
     * If the number of cards is odd, player 1 gets the extra card.
     *
     * @return an array of two Hand objects [player1Hand, player2Hand]
     */
    public Hand[] deal(List<UUID> cardIds) {
        if (cardIds.size() < 2) {
            throw new IllegalArgumentException("Need at least 2 cards to deal");
        }

        List<UUID> shuffled = new ArrayList<>(cardIds);
        Collections.shuffle(shuffled);

        int midpoint = shuffled.size() / 2;

        List<UUID> hand1Cards = new ArrayList<>(shuffled.subList(0, shuffled.size() - midpoint));
        List<UUID> hand2Cards = new ArrayList<>(shuffled.subList(shuffled.size() - midpoint, shuffled.size()));

        return new Hand[]{new Hand(hand1Cards), new Hand(hand2Cards)};
    }
}
