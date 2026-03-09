package com.dinotoptrumps.game.adapters.out;

import com.dinotoptrumps.game.domain.model.Card;

public class CardMapper {

    public static CardJpaEntity toEntity(Card card) {
        CardJpaEntity entity = new CardJpaEntity();
        entity.setId(card.getId());
        entity.setName(card.getName());
        entity.setMeaning(card.getMeaning());
        entity.setDiet(card.getDiet());
        entity.setEra(card.getEra());
        entity.setImageUrl(card.getImageUrl());
        entity.setHeight(card.getHeight());
        entity.setWeight(card.getWeight());
        entity.setIntelligence(card.getIntelligence());
        entity.setSpeed(card.getSpeed());
        entity.setStrength(card.getStrength());
        return entity;
    }

    public static Card toDomain(CardJpaEntity entity) {
        return new Card(
                entity.getId(),
                entity.getName(),
                entity.getMeaning(),
                entity.getDiet(),
                entity.getEra(),
                entity.getImageUrl(),
                entity.getHeight(),
                entity.getWeight(),
                entity.getIntelligence(),
                entity.getSpeed(),
                entity.getStrength()
        );
    }
}
