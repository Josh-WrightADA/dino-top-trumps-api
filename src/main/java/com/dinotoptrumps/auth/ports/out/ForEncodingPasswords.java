package com.dinotoptrumps.auth.ports.out;

public interface ForEncodingPasswords {
    String encode(String rawPassword);
    boolean matches(String rawPassword, String encodedPassword);
}
