package com.dinotoptrumps.game.adapters.in;

import com.dinotoptrumps.game.ports.in.ForCleaningUpGames;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class GameCleanupScheduler {

    private static final Logger log = LoggerFactory.getLogger(GameCleanupScheduler.class);

    private final ForCleaningUpGames forCleaningUpGames;

    public GameCleanupScheduler(ForCleaningUpGames forCleaningUpGames) {
        this.forCleaningUpGames = forCleaningUpGames;
    }

    @Scheduled(fixedRate = 600_000)
    public void cleanupStaleGames() {
        try {
            int cleaned = forCleaningUpGames.cleanupStaleGames();
            if (cleaned > 0) {
                log.info("Scheduled cleanup: {} stale games removed", cleaned);
            }
        } catch (Exception e) {
            log.error("Stale game cleanup failed", e);
        }
    }
}
