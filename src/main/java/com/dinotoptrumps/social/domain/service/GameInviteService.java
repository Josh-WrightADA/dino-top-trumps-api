package com.dinotoptrumps.social.domain.service;

import com.dinotoptrumps.shared.exception.NotAuthorisedException;
import com.dinotoptrumps.social.domain.exception.GameInviteExpiredException;
import com.dinotoptrumps.social.domain.exception.GameInviteNotFoundException;
import com.dinotoptrumps.social.domain.exception.NotFriendsException;
import com.dinotoptrumps.social.domain.model.GameInvite;
import com.dinotoptrumps.social.ports.in.ForManagingGameInvites;
import com.dinotoptrumps.social.ports.out.ForCheckingGameStatus;
import com.dinotoptrumps.social.ports.out.ForJoiningGameFromInvite;
import com.dinotoptrumps.social.ports.out.ForPersistingFriendships;
import com.dinotoptrumps.social.ports.out.ForPersistingGameInvites;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

public class GameInviteService implements ForManagingGameInvites {

    private static final Logger log = LoggerFactory.getLogger(GameInviteService.class);

    private final ForPersistingGameInvites inviteRepo;
    private final ForPersistingFriendships friendshipRepo;
    private final ForCheckingGameStatus gameStatusChecker;
    private final ForJoiningGameFromInvite gameJoiner;

    public GameInviteService(ForPersistingGameInvites inviteRepo,
                             ForPersistingFriendships friendshipRepo,
                             ForCheckingGameStatus gameStatusChecker,
                             ForJoiningGameFromInvite gameJoiner) {
        this.inviteRepo = inviteRepo;
        this.friendshipRepo = friendshipRepo;
        this.gameStatusChecker = gameStatusChecker;
        this.gameJoiner = gameJoiner;
    }

    @Override
    public GameInvite sendInvite(UUID inviterId, UUID inviteeId, UUID gameId) {
        if (inviterId.equals(inviteeId)) {
            throw new IllegalArgumentException("Cannot invite yourself");
        }

        if (!gameStatusChecker.isGameWaitingAndHostedBy(gameId, inviterId)) {
            throw new IllegalStateException("Game is not available for invites or you are not the host");
        }

        friendshipRepo.findAcceptedBetween(inviterId, inviteeId)
                .orElseThrow(() -> new NotFriendsException(
                        "You must be friends to invite a player to your game"));

        inviteRepo.findPendingByGameAndInvitee(gameId, inviteeId).ifPresent(existing -> {
            throw new IllegalStateException("Invite already pending for this player");
        });

        GameInvite invite = GameInvite.create(gameId, inviterId, inviteeId);
        GameInvite saved = inviteRepo.save(invite);
        log.info("event_type=GAME_INVITE_SENT gameId={} inviterId={} inviteeId={}",
                gameId, inviterId, inviteeId);
        return saved;
    }

    @Override
    public GameInvite acceptInvite(UUID inviteId, UUID acceptingUserId) {
        GameInvite invite = inviteRepo.findById(inviteId)
                .orElseThrow(() -> new GameInviteNotFoundException("Game invite not found: " + inviteId));

        if (!invite.isAddressedTo(acceptingUserId)) {
            throw new NotAuthorisedException("Not authorised to accept this invite");
        }

        if (invite.isExpired()) {
            throw new GameInviteExpiredException("This game invite has expired");
        }

        invite.accept();
        gameJoiner.joinGame(invite.getGameId(), acceptingUserId);
        GameInvite saved = inviteRepo.save(invite);
        log.info("event_type=GAME_INVITE_ACCEPTED inviteId={} acceptingUserId={}", inviteId, acceptingUserId);
        return saved;
    }

    @Override
    public GameInvite declineInvite(UUID inviteId, UUID decliningUserId) {
        GameInvite invite = inviteRepo.findById(inviteId)
                .orElseThrow(() -> new GameInviteNotFoundException("Game invite not found: " + inviteId));

        if (!invite.isAddressedTo(decliningUserId)) {
            throw new NotAuthorisedException("Not authorised to decline this invite");
        }

        invite.decline();
        GameInvite saved = inviteRepo.save(invite);
        log.info("event_type=GAME_INVITE_DECLINED inviteId={} decliningUserId={}", inviteId, decliningUserId);
        return saved;
    }

    @Override
    public List<GameInvite> getPendingInvites(UUID userId) {
        return inviteRepo.findPendingByInviteeId(userId);
    }

    @Override
    public int cleanupExpiredInvites() {
        List<GameInvite> expired = inviteRepo.findExpiredPending(Instant.now());
        for (GameInvite invite : expired) {
            invite.expire();
            inviteRepo.save(invite);
        }
        if (!expired.isEmpty()) {
            log.info("event_type=GAME_INVITES_EXPIRED count={}", expired.size());
        }
        return expired.size();
    }
}
