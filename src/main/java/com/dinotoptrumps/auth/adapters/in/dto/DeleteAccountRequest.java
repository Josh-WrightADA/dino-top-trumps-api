package com.dinotoptrumps.auth.adapters.in.dto;

import jakarta.validation.constraints.NotBlank;

public record DeleteAccountRequest(
        @NotBlank String password
) {}
