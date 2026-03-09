package com.dinotoptrumps.game.domain.model;

import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class HandTest {

    @Test
    void topCard_returnsFirstCardWithoutRemoving() {
        UUID card1 = UUID.randomUUID();
        UUID card2 = UUID.randomUUID();
        Hand hand = new Hand(List.of(card1, card2));

        assertEquals(card1, hand.topCard());
        assertEquals(2, hand.size());
    }

    @Test
    void removeTopCard_removesAndReturnsFirst() {
        UUID card1 = UUID.randomUUID();
        UUID card2 = UUID.randomUUID();
        Hand hand = new Hand(List.of(card1, card2));

        assertEquals(card1, hand.removeTopCard());
        assertEquals(1, hand.size());
        assertEquals(card2, hand.topCard());
    }

    @Test
    void addCardsToBottom_addsToEnd() {
        UUID card1 = UUID.randomUUID();
        UUID card2 = UUID.randomUUID();
        UUID card3 = UUID.randomUUID();
        Hand hand = new Hand(List.of(card1));

        hand.addCardsToBottom(List.of(card2, card3));

        assertEquals(3, hand.size());
        assertEquals(card1, hand.topCard());
    }

    @Test
    void emptyHand_topCardThrows() {
        Hand hand = Hand.empty();
        assertThrows(IllegalStateException.class, hand::topCard);
    }

    @Test
    void emptyHand_removeTopCardThrows() {
        Hand hand = Hand.empty();
        assertThrows(IllegalStateException.class, hand::removeTopCard);
    }

    @Test
    void isEmpty_returnsTrueForEmptyHand() {
        UUID card1 = UUID.randomUUID();
        Hand emptyHand = Hand.empty();
        Hand notEmptyHand = new Hand(List.of(card1));
        assertTrue(emptyHand.isEmpty());
        assertFalse(notEmptyHand.isEmpty());
    }

    @Test
    void getCardIds_returnsUnmodifiableList() {
        Hand hand = new Hand(List.of(UUID.randomUUID()));
        List<UUID> ids = hand.getCardIds();
        assertThrows(Exception.class, () -> {
            ids.add(hand.topCard());
        });
    }
}
