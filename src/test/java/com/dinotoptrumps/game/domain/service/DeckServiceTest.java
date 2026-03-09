package com.dinotoptrumps.game.domain.service;

import com.dinotoptrumps.game.domain.model.Hand;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;

class DeckServiceTest {

    private DeckService deckService;

    @BeforeEach
    void setUp() {
        deckService = new DeckService();
    }

    @Test
    void deal_evenNumberOfCards_splitEqually() {
        List<UUID> cards = createCardIds(10);
        Hand[] hands = deckService.deal(cards);
        assertEquals(5, hands[0].size());
        assertEquals(hands[0].size(), hands[1].size());
    }

    @Test
    void deal_oddNumberOfCards_player1GetsExtra() {
        List<UUID> cards = createCardIds(11);
        Hand[] hands = deckService.deal(cards);
        assertEquals(6, hands[0].size());
        assertEquals(5, hands[1].size());
    }

    @Test
    void deal_allCardsAreDistributed() {
        List<UUID> cards = createCardIds(20);
        Hand[] hands = deckService.deal(cards);
        assertEquals(20, hands[0].size() + hands[1].size());
    }

    @Test
    void deal_noCardAppearsInBothHands() {
        List<UUID> cards = createCardIds(10);
        Hand[] hands = deckService.deal(cards);
        List<UUID> handOne = hands[0].getCardIds();
        List<UUID> handTwo = hands[1].getCardIds();

        HashSet<UUID> combinedHands = new HashSet<>();
        combinedHands.addAll(handOne);
        combinedHands.addAll(handTwo);
        assertEquals(10, combinedHands.size());
    }

    @Test
    void deal_cardsAreShuffled() {
        List<UUID> cards = createCardIds(10);
        Hand[] base = deckService.deal(cards);
        List<UUID> handOne = base[0].getCardIds();
        boolean foundDifference = false;
        for (int i = 0; i < 9; i++) {
            Hand[] variant = deckService.deal(cards);
            if (!variant[0].getCardIds().equals(handOne)) {
                foundDifference = true;
            }
        }
        assertTrue(foundDifference);
    }

    @Test
    void deal_emptyDeck_returnsTwoEmptyHands() {
        Hand[] hands = deckService.deal(new ArrayList<>());
        assertTrue(hands[0].isEmpty());
        assertTrue(hands[1].isEmpty());
    }

    private List<UUID> createCardIds(int count) {
        List<UUID> ids = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            ids.add(UUID.randomUUID());
        }
        return ids;
    }
}
