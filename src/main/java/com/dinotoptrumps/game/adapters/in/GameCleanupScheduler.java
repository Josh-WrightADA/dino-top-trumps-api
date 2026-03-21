package com.dinotoptrumps.game.adapters.in;

import com.dinotoptrumps.game.ports.in.ForCleaningUpGames;
import com.dinotoptrumps.social.ports.in.ForManagingGameInvites;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class GameCleanupScheduler {

    private static final Logger log = LoggerFactory.getLogger(GameCleanupScheduler.class);

    private final ForCleaningUpGames forCleaningUpGames;
    private final ForManagingGameInvites forManagingGameInvites;

    public GameCleanupScheduler(ForCleaningUpGames forCleaningUpGames,
                                ForManagingGameInvites forManagingGameInvites) {
        this.forCleaningUpGames = forCleaningUpGames;
        this.forManagingGameInvites = forManagingGameInvites;
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

    @Scheduled(fixedRate = 60_000)
    public void cleanupExpiredInvites() {
        try {
            int expired = forManagingGameInvites.cleanupExpiredInvites();
            if (expired > 0) {
                log.info("Scheduled cleanup: {} expired game invites marked", expired);
            }
        } catch (Exception e) {
            log.error("Expired game invite cleanup failed", e);
        }
    }
}
