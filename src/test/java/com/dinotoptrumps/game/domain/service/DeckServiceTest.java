package com.dinotoptrumps.game.domain.service;

import com.dinotoptrumps.game.domain.model.Hand;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
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
        // TODO: Assert both hands have exactly 5 cards
    }

    @Test
    void deal_oddNumberOfCards_player1GetsExtra() {
        List<UUID> cards = createCardIds(11);
        Hand[] hands = deckService.deal(cards);
        // TODO: Assert player1 has 6 cards and player2 has 5
    }

    @Test
    void deal_allCardsAreDistributed() {
        List<UUID> cards = createCardIds(20);
        Hand[] hands = deckService.deal(cards);
        // TODO: Assert that hands[0].size() + hands[1].size() equals 20
        // All cards should be accounted for
    }

    @Test
    void deal_noCardAppearsInBothHands() {
        List<UUID> cards = createCardIds(10);
        Hand[] hands = deckService.deal(cards);
        // TODO: Assert no card ID appears in both hands
        // Hint: check that the intersection of both card lists is empty
    }

    @Test
    void deal_cardsAreShuffled() {
        // This is tricky to test since shuffling is random
        // TODO: Deal the same cards multiple times and verify that
        // the order isn't always the same (run 10 deals, check at least
        // one differs from the first)
    }

    @Test
    void deal_emptyDeck_returnsTwoEmptyHands() {
        Hand[] hands = deckService.deal(new ArrayList<>());
        // TODO: Assert both hands are empty
    }

    // Helper method to create a list of random card IDs
    private List<UUID> createCardIds(int count) {
        List<UUID> ids = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            ids.add(UUID.randomUUID());
        }
        return ids;
    }
}
