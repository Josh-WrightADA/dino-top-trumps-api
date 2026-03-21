package com.dinotoptrumps.game.adapters.out;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.UUID;

@Entity
@Table(name = "cards")
@Getter
@Setter
@NoArgsConstructor
public class CardJpaEntity {

    @Id
    private UUID id;

    @Column(nullable = false)
    private String name;

    private String meaning;

    private String diet;

    private String era;

    @Column(name = "image_url")
    private String imageUrl;

    @Column(columnDefinition = "TEXT")
    private String description;

    @Column(name = "fun_fact", columnDefinition = "TEXT")
    private String funFact;

    @Column(nullable = false)
    private int height;

    @Column(nullable = false)
    private int weight;

    @Column(nullable = false)
    private int intelligence;

    @Column(nullable = false)
    private int speed;

    @Column(nullable = false)
    private int strength;
}
