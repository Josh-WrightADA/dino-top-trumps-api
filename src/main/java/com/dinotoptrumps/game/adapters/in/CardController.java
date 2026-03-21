package com.dinotoptrumps.game.adapters.in;

import com.dinotoptrumps.game.adapters.in.dto.CardResponse;
import com.dinotoptrumps.game.ports.out.ForLoadingCards;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.concurrent.TimeUnit;

@RestController
@RequestMapping("/api/v1/cards")
public class CardController {

    private final ForLoadingCards forLoadingCards;

    public CardController(ForLoadingCards forLoadingCards) {
        this.forLoadingCards = forLoadingCards;
    }

    @GetMapping
    public ResponseEntity<List<CardResponse>> getAllCards() {
        List<CardResponse> cards = forLoadingCards.loadAllCards().stream()
                .map(CardResponse::from)
                .toList();
        return ResponseEntity.ok()
                .cacheControl(org.springframework.http.CacheControl.maxAge(1, TimeUnit.HOURS).cachePublic())
                .body(cards);
    }
}
