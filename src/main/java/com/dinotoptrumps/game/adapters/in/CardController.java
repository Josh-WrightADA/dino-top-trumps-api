package com.dinotoptrumps.game.adapters.in;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/cards")
public class CardController {

    // TODO: Inject ForLoadingCards

    @GetMapping
    public void getAllCards() {
        // TODO: Return all dinosaur cards
    }
}
