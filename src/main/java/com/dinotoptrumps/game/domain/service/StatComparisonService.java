package com.dinotoptrumps.game.domain.service;

import com.dinotoptrumps.game.domain.model.Card;
import com.dinotoptrumps.game.domain.model.Stat;

import java.util.UUID;

public class StatComparisonService {

    /**
     * Compares a stat between two cards.
     *
     * @return the ID of the winning card, or null if it is a draw
     */
    public UUID compare(Card card1, Card card2, Stat stat) {
        int value1 = stat.getValueFromCard(card1);
        int value2 = stat.getValueFromCard(card2);

        if (value1 > value2) {
            return card1.getId();
        } else if (value2 > value1) {
            return card2.getId();
        }
        return null;
    }
}
