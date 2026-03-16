package com.dinotoptrumps.game.adapters.in.dto;

import jakarta.validation.constraints.NotNull;

import com.dinotoptrumps.game.domain.model.Stat;

public record PlayTurnRequest(
        @NotNull Stat stat
) {}
