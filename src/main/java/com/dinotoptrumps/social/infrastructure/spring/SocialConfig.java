package com.dinotoptrumps.social.infrastructure.spring;

import com.dinotoptrumps.social.domain.service.FriendshipService;
import com.dinotoptrumps.social.domain.service.GameInviteService;
import com.dinotoptrumps.social.ports.out.ForCheckingGameStatus;
import com.dinotoptrumps.social.ports.out.ForJoiningGameFromInvite;
import com.dinotoptrumps.social.ports.out.ForLookingUpUsers;
import com.dinotoptrumps.social.ports.out.ForPersistingFriendships;
import com.dinotoptrumps.social.ports.out.ForPersistingGameInvites;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class SocialConfig {

    @Bean
    public FriendshipService friendshipService(ForPersistingFriendships friendshipRepo,
                                               ForLookingUpUsers userLookup) {
        return new FriendshipService(friendshipRepo, userLookup);
    }

    @Bean
    public GameInviteService gameInviteService(ForPersistingGameInvites inviteRepo,
                                               ForPersistingFriendships friendshipRepo,
                                               ForCheckingGameStatus gameStatusChecker,
                                               ForJoiningGameFromInvite gameJoiner) {
        return new GameInviteService(inviteRepo, friendshipRepo, gameStatusChecker, gameJoiner);
    }
}
