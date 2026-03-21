package com.dinotoptrumps.auth.infrastructure.spring;

import com.dinotoptrumps.auth.domain.service.AdminService;
import com.dinotoptrumps.auth.domain.service.AuthService;
import com.dinotoptrumps.auth.domain.service.LeaderboardService;
import com.dinotoptrumps.auth.domain.service.PasswordResetService;
import com.dinotoptrumps.auth.domain.service.ProfanityFilter;
import com.dinotoptrumps.auth.domain.service.ReportService;
import com.dinotoptrumps.auth.ports.in.ForAdminOperations;
import com.dinotoptrumps.auth.ports.in.ForReportingUsers;
import com.dinotoptrumps.auth.ports.in.ForViewingPublicProfile;
import com.dinotoptrumps.auth.ports.out.ForEncodingPasswords;
import com.dinotoptrumps.auth.ports.out.ForPersistingReports;
import com.dinotoptrumps.auth.ports.out.ForPersistingResetTokens;
import com.dinotoptrumps.auth.ports.out.ForPersistingUsers;
import com.dinotoptrumps.auth.ports.out.ForSendingEmails;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AuthConfig {

    @Bean
    public ProfanityFilter profanityFilter() {
        return ProfanityFilter.fromClasspath("profanity-words.txt");
    }

    @Bean
    public AuthService authService(ForPersistingUsers userRepository,
                                   ForEncodingPasswords passwordEncoder,
                                   ProfanityFilter profanityFilter) {
        return new AuthService(userRepository, passwordEncoder, profanityFilter);
    }

    @Bean
    public ForViewingPublicProfile forViewingPublicProfile(AuthService authService) {
        return authService;
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

    @Bean
    public ForAdminOperations forAdminOperations(ForPersistingUsers userRepository) {
        return new AdminService(userRepository);
    }

    @Bean
    public ForReportingUsers forReportingUsers(ForPersistingReports reportRepository,
                                               ForPersistingUsers userRepository) {
        return new ReportService(reportRepository, userRepository);
    }
}
