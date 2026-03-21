package com.dinotoptrumps.auth.adapters.in.dto;

import jakarta.validation.constraints.Size;

import java.util.UUID;

public record UpdateProfileRequest(
        @Size(min = 1, max = 100) String displayName,
        @Size(max = 500) String bio,
        UUID favouriteCardId
) {}
