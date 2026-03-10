package com.dinotoptrumps.auth.adapters.out;

import com.dinotoptrumps.auth.domain.model.PasswordResetToken;
import com.dinotoptrumps.auth.ports.out.ForPersistingResetTokens;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class PasswordResetTokenPersistenceAdapter implements ForPersistingResetTokens {

    private final PasswordResetTokenJpaRepository passwordResetTokenJpaRepository;

    public PasswordResetTokenPersistenceAdapter(PasswordResetTokenJpaRepository passwordResetTokenJpaRepository) {
        this.passwordResetTokenJpaRepository = passwordResetTokenJpaRepository;
    }

    @Override
    public PasswordResetToken save(PasswordResetToken token) {
        PasswordResetTokenJpaEntity entity = PasswordResetTokenMapper.toEntity(token);
        PasswordResetTokenJpaEntity saved = passwordResetTokenJpaRepository.save(entity);
        return PasswordResetTokenMapper.toDomain(saved);
    }

    @Override
    public Optional<PasswordResetToken> findByToken(String token) {
        return passwordResetTokenJpaRepository.findByToken(token)
                .map(PasswordResetTokenMapper::toDomain);
    }
}
