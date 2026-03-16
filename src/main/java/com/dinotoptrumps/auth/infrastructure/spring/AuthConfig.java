package com.dinotoptrumps.auth.infrastructure.spring;

import com.dinotoptrumps.auth.domain.service.AuthService;
import com.dinotoptrumps.auth.domain.service.LeaderboardService;
import com.dinotoptrumps.auth.domain.service.PasswordResetService;
import com.dinotoptrumps.auth.ports.out.ForEncodingPasswords;
import com.dinotoptrumps.auth.ports.out.ForPersistingResetTokens;
import com.dinotoptrumps.auth.ports.out.ForPersistingUsers;
import com.dinotoptrumps.auth.ports.out.ForSendingEmails;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AuthConfig {

    @Bean
    public AuthService authService(ForPersistingUsers userRepository,
                                   ForEncodingPasswords passwordEncoder) {
        return new AuthService(userRepository, passwordEncoder);
    }

    @Bean
    public PasswordResetService passwordResetService(ForPersistingUsers userRepository,
                                                     ForPersistingResetTokens tokenRepository,
                                                     ForSendingEmails emailSender,
                                                     ForEncodingPasswords passwordEncoder) {
        return new PasswordResetService(userRepository, tokenRepository, emailSender, passwordEncoder);
    }

    @Bean
    public LeaderboardService leaderboardService(ForPersistingUsers userRepository) {
        return new LeaderboardService(userRepository);
    }
}
