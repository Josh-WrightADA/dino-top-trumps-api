package com.dinotoptrumps.game.infrastructure.spring;

import com.dinotoptrumps.game.domain.service.DeckService;
import com.dinotoptrumps.game.domain.service.EloService;
import com.dinotoptrumps.game.domain.service.GameService;
import com.dinotoptrumps.game.domain.service.StatComparisonService;
import com.dinotoptrumps.game.ports.out.ForLoadingCards;
import com.dinotoptrumps.game.ports.out.ForPersistingGames;
import com.dinotoptrumps.game.ports.out.ForPersistingTurns;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class GameConfig {

    @Bean
    public DeckService deckService() {
        return new DeckService();
    }

    @Bean
    public StatComparisonService statComparisonService() {
        return new StatComparisonService();
    }

    @Bean
    public EloService eloService() {
        return new EloService();
    }

    @Bean
    public GameService gameService(ForPersistingGames gameRepository,
                                   ForPersistingTurns turnRepository,
                                   ForLoadingCards cardLoader,
                                   DeckService deckService,
                                   StatComparisonService statComparisonService,
                                   EloService eloService) {
        return new GameService(gameRepository, turnRepository, cardLoader,
                deckService, statComparisonService, eloService);
    }
}
