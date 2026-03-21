package com.dinotoptrumps.game.adapters.in.dto;

import com.dinotoptrumps.game.domain.model.Card;

import java.util.UUID;

public record CardResponse(
        UUID id,
        String name,
        String meaning,
        String diet,
        String era,
        String imageUrl,
        String description,
        String funFact,
        int height,
        int weight,
        int intelligence,
        int speed,
        int strength
) {
    public static CardResponse from(Card card) {
        return new CardResponse(
                card.getId(),
                card.getName(),
                card.getMeaning(),
                card.getDiet(),
                card.getEra(),
                card.getImageUrl(),
                card.getDescription(),
                card.getFunFact(),
                card.getHeight(),
                card.getWeight(),
                card.getIntelligence(),
                card.getSpeed(),
                card.getStrength()
        );
    }
}
