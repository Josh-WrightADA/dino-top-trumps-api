package com.dinotoptrumps.auth.infrastructure.spring;

import com.dinotoptrumps.auth.adapters.out.UserJpaRepository;
import com.dinotoptrumps.auth.domain.service.AuthService;
import com.dinotoptrumps.auth.domain.service.PasswordResetService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AuthConfig {

    // TODO: Wire domain services with adapter implementations
    // Example:
    // @Bean
    // public AuthService authService(ForPersistingUsers userRepository) {
    //     return new AuthService(userRepository);
    // }
}
