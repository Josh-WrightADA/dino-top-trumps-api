package com.dinotoptrumps.game.domain.model;

import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class HandTest {

    @Test
    void topCard_returnsFirstCardWithoutRemoving() {
        UUID card1 = UUID.randomUUID();
        UUID card2 = UUID.randomUUID();
        Hand hand = new Hand(List.of(card1, card2));

        // TODO: Assert topCard() returns card1
        // TODO: Assert hand still has 2 cards (topCard doesn't remove)
    }

    @Test
    void removeTopCard_removesAndReturnsFirst() {
        UUID card1 = UUID.randomUUID();
        UUID card2 = UUID.randomUUID();
        Hand hand = new Hand(List.of(card1, card2));

        // TODO: Assert removeTopCard() returns card1
        // TODO: Assert hand now has 1 card
        // TODO: Assert topCard() now returns card2
    }

    @Test
    void addCardsToBottom_addsToEnd() {
        UUID card1 = UUID.randomUUID();
        UUID card2 = UUID.randomUUID();
        UUID card3 = UUID.randomUUID();
        Hand hand = new Hand(List.of(card1));

        hand.addCardsToBottom(List.of(card2, card3));

        // TODO: Assert hand has 3 cards
        // TODO: Assert topCard() is still card1 (new cards went to bottom)
    }

    @Test
    void emptyHand_topCardThrows() {
        Hand hand = Hand.empty();
        // TODO: Assert that calling topCard() throws IllegalStateException
    }

    @Test
    void emptyHand_removeTopCardThrows() {
        Hand hand = Hand.empty();
        // TODO: Assert that calling removeTopCard() throws IllegalStateException
    }

    @Test
    void isEmpty_returnsTrueForEmptyHand() {
        // TODO: Test isEmpty() on an empty hand and a non-empty hand
    }

    @Test
    void getCardIds_returnsUnmodifiableList() {
        Hand hand = new Hand(List.of(UUID.randomUUID()));
        // TODO: Assert that trying to add to getCardIds() throws an exception
        // This verifies the list is truly unmodifiable
    }
}
