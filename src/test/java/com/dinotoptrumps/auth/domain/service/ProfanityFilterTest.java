package com.dinotoptrumps.auth.domain.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ProfanityFilterTest {

    private ProfanityFilter filter;

    @BeforeEach
    void setUp() {
        filter = new ProfanityFilter(Set.of("idiot", "moron", "noob"));
    }

    @Test
    void detectsBlockedWord() {
        assertTrue(filter.containsProfanity("you are an idiot"));
    }

    @Test
    void detectsCaseInsensitive() {
        assertTrue(filter.containsProfanity("MORON alert"));
    }

    @Test
    void allowsCleanText() {
        assertFalse(filter.containsProfanity("DinoKing42"));
    }

    @Test
    void handlesNullAndEmpty() {
        assertFalse(filter.containsProfanity(null));
        assertFalse(filter.containsProfanity(""));
        assertFalse(filter.containsProfanity("   "));
    }

    @Test
    void validateThrowsOnProfanity() {
        assertThrows(IllegalArgumentException.class, () -> filter.validate("noob name", "Username"));
    }

    @Test
    void validateAllowsCleanText() {
        assertDoesNotThrow(() -> filter.validate("DinoKing42", "Username"));
    }
}
