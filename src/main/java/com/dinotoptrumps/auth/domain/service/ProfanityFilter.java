package com.dinotoptrumps.auth.domain.service;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

public class ProfanityFilter {

    private final Pattern pattern;

    public ProfanityFilter(Set<String> blockedWords) {
        String joined = blockedWords.stream()
                .map(Pattern::quote)
                .collect(Collectors.joining("|"));
        this.pattern = Pattern.compile("\\b(" + joined + ")\\b", Pattern.CASE_INSENSITIVE);
    }

    public static ProfanityFilter fromClasspath(String resourcePath) {
        try (InputStream is = ProfanityFilter.class.getClassLoader().getResourceAsStream(resourcePath)) {
            if (is == null) {
                throw new IllegalStateException("Profanity word list not found: " + resourcePath);
            }
            Set<String> words = new HashSet<>();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(is, StandardCharsets.UTF_8))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    String trimmed = line.trim();
                    if (!trimmed.isEmpty() && !trimmed.startsWith("#")) {
                        words.add(trimmed);
                    }
                }
            }
            return new ProfanityFilter(words);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to load profanity word list", e);
        }
    }

    public boolean containsProfanity(String text) {
        if (text == null || text.isBlank()) {
            return false;
        }
        return pattern.matcher(text).find();
    }

    public void validate(String text, String fieldName) {
        if (containsProfanity(text)) {
            throw new IllegalArgumentException(fieldName + " contains inappropriate language");
        }
    }
}
