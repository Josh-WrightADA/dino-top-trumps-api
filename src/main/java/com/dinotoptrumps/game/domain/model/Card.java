package com.dinotoptrumps.game.domain.model;

import java.util.UUID;

public class Card {

    private final UUID id;
    private final String name;
    private final String meaning;
    private final String diet;
    private final String era;
    private final String imageUrl;
    private final int height;
    private final int weight;
    private final int intelligence;
    private final int speed;
    private final int strength;

    public Card(UUID id, String name, String meaning, String diet, String era,
                String imageUrl, int height, int weight, int intelligence,
                int speed, int strength) {
        this.id = id;
        this.name = name;
        this.meaning = meaning;
        this.diet = diet;
        this.era = era;
        this.imageUrl = imageUrl;
        this.height = height;
        this.weight = weight;
        this.intelligence = intelligence;
        this.speed = speed;
        this.strength = strength;
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getMeaning() {
        return meaning;
    }

    public String getDiet() {
        return diet;
    }

    public String getEra() {
        return era;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public int getHeight() {
        return height;
    }

    public int getWeight() {
        return weight;
    }

    public int getIntelligence() {
        return intelligence;
    }

    public int getSpeed() {
        return speed;
    }

    public int getStrength() {
        return strength;
    }
}
