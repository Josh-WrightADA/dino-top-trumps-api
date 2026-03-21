package com.dinotoptrumps.social.infrastructure.spring;

import com.dinotoptrumps.auth.ports.out.ForPersistingUsers;
import com.dinotoptrumps.game.ports.in.ForJoiningGame;
import com.dinotoptrumps.game.ports.out.ForPersistingGames;
import com.dinotoptrumps.social.domain.service.FriendshipService;
import com.dinotoptrumps.social.domain.service.GameInviteService;
import com.dinotoptrumps.social.ports.out.ForPersistingFriendships;
import com.dinotoptrumps.social.ports.out.ForPersistingGameInvites;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SocialConfig {

    @Bean
    public FriendshipService friendshipService(ForPersistingFriendships friendshipRepo,
                                               ForPersistingUsers userRepo) {
        return new FriendshipService(friendshipRepo, userRepo);
    }

    @Bean
    public GameInviteService gameInviteService(ForPersistingGameInvites inviteRepo,
                                               ForPersistingFriendships friendshipRepo,
                                               ForPersistingGames gameRepo,
                                               ForJoiningGame forJoiningGame) {
        return new GameInviteService(inviteRepo, friendshipRepo, gameRepo, forJoiningGame);
    }
}
