package com.dinotoptrumps.auth.domain.service;

import com.dinotoptrumps.auth.domain.exception.InvalidCredentialsException;
import com.dinotoptrumps.auth.domain.model.AccountStatus;
import com.dinotoptrumps.auth.domain.model.Role;
import com.dinotoptrumps.auth.domain.model.User;
import com.dinotoptrumps.auth.ports.out.ForPersistingUsers;
import com.dinotoptrumps.support.TestFixtures;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AdminServiceTest {

    private ForPersistingUsers userRepository;
    private AdminService adminService;

    @BeforeEach
    void setUp() {
        userRepository = mock(ForPersistingUsers.class);
        adminService = new AdminService(userRepository);
    }

    @Nested
    class GetAllUsers {

        @Test
        void getAllUsers_returnsAllUsers() {
            UUID id1 = UUID.randomUUID();
            UUID id2 = UUID.randomUUID();
            List<User> users = List.of(
                    TestFixtures.createUser(id1, "player1", Role.ADMIN, AccountStatus.ACTIVE),
                    TestFixtures.createUser(id2, "player2", Role.PLAYER, AccountStatus.ACTIVE)
            );
            when(userRepository.findAll()).thenReturn(users);

            List<User> result = adminService.getAllUsers();

            assertEquals(2, result.size(), "should return both seeded users");
        }
    }

    @Nested
    class BanUser {

        @Test
        void banUser_bansActivePlayer() {
            UUID id = UUID.randomUUID();
            User player = TestFixtures.createUser(id, "playerToban", Role.PLAYER, AccountStatus.ACTIVE);
            when(userRepository.findById(id)).thenReturn(Optional.of(player));
            when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

            User result = adminService.banUser(id);

            assertEquals(AccountStatus.BANNED, result.getStatus());
        }

        @Test
        void banUser_cannotBanAdmin() {
            UUID id = UUID.randomUUID();
            User admin = TestFixtures.createUser(id, "adminUser", Role.ADMIN, AccountStatus.ACTIVE);
            when(userRepository.findById(id)).thenReturn(Optional.of(admin));

            assertThrows(IllegalStateException.class, () -> adminService.banUser(id));
        }

        @Test
        void banUser_throwsWhenNotFound() {
            UUID id = UUID.randomUUID();
            when(userRepository.findById(id)).thenReturn(Optional.empty());

            assertThrows(InvalidCredentialsException.class, () -> adminService.banUser(id));
        }
    }

    @Nested
    class UnbanUser {

        @Test
        void unbanUser_unbansBannedUser() {
            UUID id = UUID.randomUUID();
            User banned = TestFixtures.createUser(id, "bannedUser", Role.PLAYER, AccountStatus.BANNED);
            when(userRepository.findById(id)).thenReturn(Optional.of(banned));
            when(userRepository.save(any(User.class))).thenAnswer(inv -> inv.getArgument(0));

            User result = adminService.unbanUser(id);

            assertEquals(AccountStatus.ACTIVE, result.getStatus());
        }
    }
}
