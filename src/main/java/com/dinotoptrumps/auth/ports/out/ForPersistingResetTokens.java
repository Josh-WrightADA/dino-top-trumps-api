package com.dinotoptrumps.auth.ports.out;

import com.dinotoptrumps.auth.domain.model.PasswordResetToken;

import java.util.Optional;

public interface ForPersistingResetTokens {
    PasswordResetToken save(PasswordResetToken token);
    Optional<PasswordResetToken> findByToken(String token);
}
