package com.dinotoptrumps.game.domain.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

public class Hand {

    private final List<UUID> cardIds;

    public Hand(List<UUID> cardIds) {
        this.cardIds = new ArrayList<>(cardIds);
    }

    public static Hand empty() {
        return new Hand(new ArrayList<>());
    }

    public UUID topCard() {
        if (cardIds.isEmpty()) {
            throw new IllegalStateException("Hand is empty");
        }
        return cardIds.getFirst();
    }

    public UUID removeTopCard() {
        if (cardIds.isEmpty()) {
            throw new IllegalStateException("Hand is empty");
        }
        return cardIds.removeFirst();
    }

    public void addCardsToBottom(List<UUID> cards) {
        cardIds.addAll(cards);
    }

    public int size() {
        return cardIds.size();
    }

    public boolean isEmpty() {
        return cardIds.isEmpty();
    }

    public List<UUID> getCardIds() {
        return Collections.unmodifiableList(cardIds);
    }
}
